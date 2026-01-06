# BAJET APPLICATION COMPREHENSIVE CODE ANALYSIS REPORT

## SUMMARY
Found 32 issues across multiple categories including data layer, UI, architecture, and best practices.

---

## 1. DATA LAYER ISSUES

### 1.1 Missing Foreign Key Constraint on Account Entity
**Location:** Account.kt:1-13
**Severity:** High
**Category:** Data Layer - Entity Relationships
**Description:** 
The Account entity has a `groupId` field that references AccountGroup, but no ForeignKey constraint is defined. This means the database won't enforce referential integrity - you can delete an AccountGroup while accounts still reference it.

**Current Code:**
```kotlin
@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,  // References AccountGroup but no FK constraint
    val name: String,
    val balance: Double
)
```

**Recommended Fix:**
Add ForeignKey constraint and index:
```kotlin
@Entity(
    tableName = "account",
    foreignKeys = [
        ForeignKey(
            entity = AccountGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE  // or RESTRICT to prevent deletion
        )
    ],
    indices = [Index("groupId")]
)
data class Account(...)
```

---

### 1.2 Missing Index on Account.groupId
**Location:** Account.kt:1-13
**Severity:** Medium
**Category:** Data Layer - Performance
**Description:**
AccountDao.getAllAccountsAndGroup() performs a JOIN on account.groupId, but there's no index. This can cause slow queries with large datasets.

**Recommended Fix:**
See fix in 1.1 - add indices parameter to @Entity.

---

### 1.3 Missing Index on Budget.categoryId (though marked unique)
**Location:** Budget.kt:8-21
**Severity:** Low
**Category:** Data Layer - Consistency
**Description:**
The unique index on categoryId is present, but Budget entity doesn't have a ForeignKey constraint to Category entity. This should be enforced.

**Recommended Fix:**
```kotlin
@Entity(
    "budget",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"], unique = true)
    ]
)
data class Budget(...)
```

---

### 1.4 Database Migration Not Configured
**Location:** CashFlowDatabase.kt:68
**Severity:** Critical
**Category:** Data Layer - Migration
**Description:**
Using `.fallbackToDestructiveMigration()` in production will DELETE all user data when the schema changes. This is not acceptable for a financial app.

**Current Code:**
```kotlin
.fallbackToDestructiveMigration()
.build()
```

**Recommended Fix:**
Create proper migration files and remove fallbackToDestructiveMigration:
```kotlin
.addMigrations(/* your migrations here */)
.build()
```

---

### 1.5 N+1 Query Risk in CashFlowDao.getCashFlowSummary
**Location:** CashFlowDao.kt:36-51
**Severity:** Medium
**Category:** Data Layer - Query Efficiency
**Description:**
The query uses multiple CASE WHEN statements but doesn't group by the type field. Could be optimized to avoid multiple aggregations.

**Recommended Fix:**
Consider simplifying or splitting the query to be more explicit about grouping.

---

### 1.6 Unsafe Type Conversion in AddCashFlowViewModel
**Location:** AddCashFlowViewModel.kt:133
**Severity:** Medium
**Category:** Data Layer - Input Validation
**Description:**
Direct `.toDouble()` call without null safety check could throw NumberFormatException.

**Current Code:**
```kotlin
fun toCashFlow(): CashFlow {
    val finalAmount = if (this.amount.isEmpty()) 0.0 else this.amount.toDouble()
```

**Recommended Fix:**
```kotlin
val finalAmount = try {
    if (this.amount.isEmpty()) 0.0 else this.amount.toDouble()
} catch (e: NumberFormatException) {
    0.0  // or log error
}
```

---

### 1.7 Unsafe Type Conversion in AddAccountViewModel
**Location:** AddAccountViewModel.kt:40
**Severity:** Medium
**Category:** Data Layer - Input Validation
**Description:**
Direct `.toDouble()` call without validation:

**Current Code:**
```kotlin
balance = amount.value.toDouble(),
```

**Recommended Fix:**
Add try-catch or use toDoubleOrNull():
```kotlin
balance = amount.value.toDoubleOrNull() ?: 0.0,
```

---

### 1.8 Unsafe Type Conversion in AddBudgetViewModel
**Location:** AddBudgetViewModel.kt:41, 62
**Severity:** Medium
**Category:** Data Layer - Input Validation
**Description:**
Direct `.toDouble()` calls without null safety in form validation and insertion.

**Recommended Fix:**
Use toDoubleOrNull() and handle null cases:
```kotlin
fun isFormsValid(): Boolean {
    val amount = addBudgetData.amount.toDoubleOrNull() ?: return false
    return amount > 0 && addBudgetData.categoryId != 0
}
```

---

### 1.9 Unsafe Type Conversion in CurrencyTextField
**Location:** CurrencyTextField.kt:57
**Severity:** Medium
**Category:** Data Layer - Input Validation
**Description:**
Direct `.toInt()` call without null safety:

**Current Code:**
```kotlin
val formattedText = numberFormatter.format(originalText.toInt())
```

**Recommended Fix:**
```kotlin
val formattedText = originalText.toIntOrNull()?.let { 
    numberFormatter.format(it) 
} ?: originalText
```

---

### 1.10 Poor Error Handling in Database Initialization
**Location:** CashFlowDatabase.kt:89-90, 108-110, 117-119
**Severity:** High
**Category:** Data Layer - Error Handling
**Description:**
Using `e.printStackTrace()` instead of proper logging. Errors are silently swallowed with no way to detect initialization failures.

**Current Code:**
```kotlin
} catch (e: JSONException) {
    e.printStackTrace()
}
```

**Recommended Fix:**
```kotlin
} catch (e: JSONException) {
    Timber.e(e, "Failed to parse JSON for categories")
}
```

---

### 1.11 Inconsistent Transaction Handling
**Location:** BudgetRepository.kt:42, 78, 88; CashFlowDao.kt:29, 56, 63, 97; BudgetDao.kt:19, 36, 64
**Severity:** Medium
**Category:** Data Layer - Transactions
**Description:**
`@Transaction` annotations are used in DAOs and Repository, but the same transaction is marked in both places (cascade transactions). This is redundant and can cause issues.

**Recommended Fix:**
Use @Transaction only at the Repository level where business logic happens, not in DAOs:
```kotlin
// In Repository
@Transaction
override suspend fun insertBudget(budget: Budget, amount: Double): Long {
    // multi-step operation
}

// In DAO - remove @Transaction if used in repository
@Query(...)
suspend fun insert(budget: Budget): Long
```

---

## 2. VIEWMODEL & STATE MANAGEMENT ISSUES

### 2.1 Missing Error Handling in AddCashFlowViewModel
**Location:** AddCashFlowViewModel.kt:70-78, 107-116
**Severity:** High
**Category:** ViewModel - Exception Handling
**Description:**
insertCashFlow() and updateCashFlow() have no error handling. If DB operations fail, user gets no feedback.

**Current Code:**
```kotlin
fun insertCashFlow() {
    viewModelScope.launch {
        val cashFlow = addCashFlowData.toCashFlow()
        withContext(Dispatchers.IO) {
            addCashFlowRepository.insertCashFlow(cashFlow)
            accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
        }
    }
}
```

**Recommended Fix:**
Add error handling and UI feedback:
```kotlin
private val _errorEvent = Channel<String>()
val errorEvent = _errorEvent.receiveAsFlow()

fun insertCashFlow() {
    viewModelScope.launch {
        try {
            val cashFlow = addCashFlowData.toCashFlow()
            withContext(Dispatchers.IO) {
                addCashFlowRepository.insertCashFlow(cashFlow)
                accountRepository.updateAmount(cashFlow.accountId, cashFlow.amount)
            }
        } catch (e: Exception) {
            _errorEvent.send("Failed to add cash flow: ${e.message}")
        }
    }
}
```

---

### 2.2 Race Condition Risk in HomeViewModel
**Location:** HomeViewModel.kt:55, 64-77
**Severity:** High
**Category:** ViewModel - Concurrency
**Description:**
updateJob can be cancelled while emitting to _uiState, causing missed updates or inconsistent state.

**Current Code:**
```kotlin
private var updateJob: Job? = null

private fun updateCashFlow(newMonth: Calendar = Calendar.getInstance()) {
    updateJob?.cancel()  // Cancels previous job
    updateJob = viewModelScope.launch {
        // Long running combine operation
        combine(...).collect {
            _uiState.value = it  // Can be cancelled mid-update
        }
    }
}
```

**Recommended Fix:**
```kotlin
private fun updateCashFlow(newMonth: Calendar = Calendar.getInstance()) {
    viewModelScope.launch {
        val (startDate, endDate) = DateUtils.getStartAndEndDate(newMonth)
        try {
            combine(...).collect {
                _uiState.value = it
            }
        } catch (e: CancellationException) {
            // Expected when switching months
        }
    }
}
```

---

### 2.3 Missing Error State in AnalyticsViewModel
**Location:** AnalyticsViewModel.kt:95-119
**Severity:** Medium
**Category:** ViewModel - State Management
**Description:**
Error state is set but never cleared on successful operations after an error.

**Current Code:**
```kotlin
private fun updateAnalytics(month: Calendar = Calendar.getInstance(), type: String) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            // ...
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Unknown error occurred"
            )
        }
    }
    // Missing: need to set isLoading = false on success
}
```

**Recommended Fix:**
```kotlin
try {
    // ... combine and collect
    combine(...).collect { newState ->
        _uiState.value = newState.copy(isLoading = false, error = null)
    }
} catch (e: Exception) {
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        error = e.message ?: "Unknown error"
    )
}
```

---

### 2.4 Unsafe Null Access in CategoriesViewModel
**Location:** CategoriesViewModel.kt:70
**Severity:** High
**Category:** ViewModel - Null Safety
**Description:**
Using `!!` operator without null safety check:

**Current Code:**
```kotlin
fun updateCategory() {
    viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.updateCategory(
            selectedCategoryToEdit!!.copy(  // Unsafe !!
                name = categoryName, emoji = emoji
            )
        )
    }
}
```

**Recommended Fix:**
```kotlin
fun updateCategory() {
    selectedCategoryToEdit?.let { category ->
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.updateCategory(
                category.copy(name = categoryName, emoji = emoji)
            )
        }
    } ?: run {
        // Handle case where no category is selected
        Timber.w("Attempted to update null category")
    }
}
```

---

### 2.5 Missing Loading State in EditBudgetViewModel
**Location:** EditBudgetViewModel.kt:70-77
**Severity:** Medium
**Category:** ViewModel - State Management
**Description:**
updateBudgetAmount() has no loading or error state, and silently fails if amount is invalid.

**Recommended Fix:**
Add error handling and state updates:
```kotlin
private fun updateBudgetAmount(amount: String) {
    val doubleAmount = amount.toDoubleOrNull()
    if (doubleAmount == null || doubleAmount <= 0) {
        // Handle error - should notify UI
        return
    }
    viewModelScope.launch {
        try {
            _uiState.update { it.copy(isLoading = true) }
            budgetRepository.updateBudgetEntry(
                id = _uiState.value.budgetMonthId,
                amount = doubleAmount
            )
            _uiState.update { it.copy(isLoading = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }
}
```

---

### 2.6 Unsafe Null Access in BudgetScreen
**Location:** BudgetScreen.kt:113
**Severity:** Medium
**Category:** ViewModel - Null Safety
**Description:**
Using `!!` operator without safety:

**Current Code:**
```kotlin
budget = budgetItem.budget!!,  // Can crash if budget is null
```

**Recommended Fix:**
```kotlin
budget = budgetItem.budget ?: 0.0,  // Default to 0 if null
```

---

## 3. UI/COMPOSE ISSUES

### 3.1 Missing Error Display in HomeScreen
**Location:** HomeScreen.kt:76-83
**Severity:** Medium
**Category:** UI - Error Handling
**Description:**
Error messages shown in snackbar but no persistent error state visible. User might miss error notification.

**Recommended Fix:**
Add retry button and error persistence:
```kotlin
is HomeUiEvent.ShowError -> {
    scope.launch {
        snackbarHostState.showSnackbar(
            message = event.message,
            actionLabel = "Retry",
            duration = SnackbarDuration.Long
        ).let { result ->
            if (result == SnackbarResult.ActionPerformed) {
                // Retry action
            }
        }
    }
}
```

---

### 3.2 LaunchedEffect Called Every Recomposition
**Location:** AddCashFlowScreen.kt:90-92
**Severity:** High
**Category:** UI - Performance
**Description:**
LaunchedEffect triggers categoryViewModel.getCategories() every time categories state changes, causing infinite loops.

**Current Code:**
```kotlin
val categories by categoryViewModel.categories.collectAsState()

LaunchedEffect(categories) {
    categoryViewModel.getCategories()  // Depends on its own output!
}
```

**Recommended Fix:**
```kotlin
LaunchedEffect(Unit) {  // Only run once on composition
    categoryViewModel.getCategories()
}
```

---

### 3.3 Large Composable Function
**Location:** AddCashFlowScreen.kt (391 lines)
**Severity:** Medium
**Category:** UI - Code Organization
**Description:**
AddCashFlowScreen is 391 lines - too large for proper recomposition tracking and testing.

**Recommended Fix:**
Break into smaller composable functions:
```kotlin
@Composable
private fun CashFlowTypeSelector(...)

@Composable
private fun AmountInput(...)

@Composable
private fun CategorySelector(...)
```

---

### 3.4 Multiple mutableStateOf in Route Function
**Location:** AddCashFlowRoute:74-76
**Severity:** Medium
**Category:** UI - State Management
**Description:**
State logic mixed in Route function. Should use ViewModel.

**Current Code:**
```kotlin
fun AddCashFlowRoute(...) {
    val shouldShowCategoryDialog = rememberSaveable { mutableStateOf(false) }
    val shouldShowDatePickerDialog = rememberSaveable { mutableStateOf(false) }
    val showAccountDialog = rememberSaveable { mutableStateOf(false) }
```

**Recommended Fix:**
Move to AddCashFlowViewModel:
```kotlin
data class AddCashFlowUiState(
    val shouldShowCategoryDialog: Boolean = false,
    val shouldShowDatePickerDialog: Boolean = false,
    val showAccountDialog: Boolean = false,
)
```

---

### 3.5 Missing Loading State in AnalyticsScreen
**Location:** AnalyticsScreen.kt (entire file)
**Severity:** Medium
**Category:** UI - User Feedback
**Description:**
No loading indicator shown when isLoading = true.

**Recommended Fix:**
Add loading indicator:
```kotlin
if (uiState.isLoading) {
    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
}
```

---

### 3.6 Hardcoded Dimension Values
**Location:** Various Screen files
**Severity:** Low
**Category:** UI - Best Practices
**Description:**
Dimensions hardcoded (e.g., `dp(8)`, `dp(16)`) instead of using theme constants.

**Recommended Fix:**
Create and use dimension constants in theme or resources.

---

## 4. ARCHITECTURE & DEPENDENCY INJECTION ISSUES

### 4.1 Inconsistent Repository Interface vs Implementation Names
**Location:** All repository files
**Severity:** Low
**Category:** Architecture - Naming
**Description:**
Repository interfaces have no "Impl" suffix for implementations. While correct, the pattern is inconsistent across different types.

**Current:** `CashFlowRepository` (interface) and `CashFlowRepositoryImpl` (impl)
**Recommendation:** Keep consistent naming - this is actually fine as-is.

---

### 4.2 DAO Methods Missing Suspend Modifier
**Location:** AccountDao.kt:15, 25; CategoryDao.kt:16, 22, 25
**Severity:** Medium
**Category:** Architecture - Coroutines
**Description:**
Insert/delete/update methods in DAOs should be suspend functions but some are not.

**Current Code (AccountDao):**
```kotlin
@Insert(onConflict = REPLACE)
fun insert(account: Account)  // Should be suspend

@Query("UPDATE account SET balance = balance + :amount WHERE id = :accountId")
suspend fun updateAmount(...)  // This one is correct
```

**Recommended Fix:**
```kotlin
@Insert(onConflict = REPLACE)
suspend fun insert(account: Account)

@Delete
suspend fun deleteCategory(category: Category)

@Update
suspend fun updateCategory(category: Category)
```

---

### 4.3 Repository Pattern Not Fully Applied
**Location:** CashFlowRepositoryImpl.kt:31-33
**Severity:** Medium
**Category:** Architecture - Consistency
**Description:**
insertCashFlow() in CashFlowRepository is not suspended but should be for consistency.

**Current Code:**
```kotlin
interface CashFlowRepository {
    fun insertCashFlow(cashFlow: CashFlow)  // Not suspended
    suspend fun deleteCashFlow(cashFlow: CashFlow)  // Suspended
}
```

**Recommended Fix:**
```kotlin
interface CashFlowRepository {
    suspend fun insertCashFlow(cashFlow: CashFlow)
    suspend fun deleteCashFlow(cashFlow: CashFlow)
}
```

---

### 4.4 Missing Dependency Injection for ViewModels in Some Cases
**Location:** AddCashFlowRoute.kt:72
**Severity:** Medium
**Category:** Architecture - Consistency
**Description:**
Two ViewModels injected in the Route function directly. CategoriesViewModel should be managed differently.

**Current Code:**
```kotlin
fun AddCashFlowRoute(
    viewModel: AddCashFlowViewModel = hiltViewModel(),
    categoryViewModel: CategoriesViewModel = hiltViewModel()  // Unused category injection
)
```

**Recommended Fix:**
Initialize categoryViewModel only inside the screen, not in route.

---

## 5. SECURITY & BEST PRACTICES ISSUES

### 5.1 Hardcoded Category IDs for Income/Expenses
**Location:** Multiple files
**Severity:** High
**Category:** Security - Magic Numbers
**Description:**
IDs 1 and 2 are hardcoded for Income and Expenses categories. If database is initialized differently, app breaks.

**Occurrences:**
- AddCashFlowViewModel.kt:139
- CashFlowData.kt (implicitly)
- CategoriesViewModel.kt:82

**Current Code:**
```kotlin
categoryId = if (selectedCashflowTypeIndex == 0) 1 else categoryId,  // Hardcoded 1
_categories.value = it.filter { category -> category.id != 1 && category.id != 2 }  // Hardcoded 1, 2
```

**Recommended Fix:**
Create constants or use a configuration:
```kotlin
object CategoryConstants {
    const val INCOME_CATEGORY_ID = 1
    const val EXPENSES_CATEGORY_ID = 2
}
```

---

### 5.2 Hardcoded Account ID
**Location:** AddCashFlowViewModel.kt:130
**Severity:** Medium
**Category:** Security - Magic Numbers
**Description:**
Default account ID hardcoded as 1.

**Current Code:**
```kotlin
val selectedAccount: Account = Account(id = 1, groupId = 1, name = "Cash", balance = 0.0)
```

**Recommended Fix:**
Load default account from database or preferences.

---

### 5.3 Hardcoded Account Group ID
**Location:** CashFlowDatabase.kt:116
**Severity:** Medium
**Category:** Security - Magic Numbers
**Description:**
Default account group hardcoded as 1.

**Current Code:**
```kotlin
dao.insert(Account(id = 1, name = "Cash", balance = 0.0, groupId = 1))
```

**Recommended Fix:**
Load from raw resources or verify the group exists.

---

### 5.4 No Input Validation for Category Names
**Location:** CategoriesViewModel.kt:34-37, 50-55, 62-68
**Severity:** Medium
**Category:** Security - Input Validation
**Description:**
Category names accepted without length or content validation.

**Recommended Fix:**
```kotlin
fun updateCategoryName(value: String) {
    if (value.length > MAX_CATEGORY_NAME_LENGTH) return
    if (value.trim().isEmpty()) return
    categoryName = value
}
```

---

### 5.5 Passcode Stored in Plain DataStore
**Location:** SettingsManager.kt:38-40
**Severity:** Critical
**Category:** Security - Data Protection
**Description:**
Passcode stored in plain text in DataStore. Should be encrypted.

**Current Code:**
```kotlin
suspend fun setPasscode(passcode: String) {
    dataStore.edit { it[PASSCODE] = passcode }  // Plain text!
}
```

**Recommended Fix:**
Encrypt sensitive data:
```kotlin
suspend fun setPasscode(passcode: String) {
    val encrypted = encryptionManager.encrypt(passcode)
    dataStore.edit { it[PASSCODE] = encrypted }
}
```

---

### 5.6 No ProGuard Rules for Release Build
**Location:** build.gradle.kts:31; proguard-rules.pro
**Severity:** Medium
**Category:** Security - Obfuscation
**Description:**
Release build has isMinifyEnabled = false. Code is not obfuscated, exposed to decompilation.

**Recommended Fix:**
```kotlin
release {
    isMinifyEnabled = true
    shrinkResources = true
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

---

### 5.7 Missing DataStore Encryption
**Location:** SettingsManager.kt (entire file)
**Severity:** High
**Category:** Security - Data Protection
**Description:**
DataStore preferences stored unencrypted. Financial data should be encrypted.

**Recommended Fix:**
Use EncryptedSharedPreferences or encrypted DataStore:
```kotlin
val encryptedDataStore = EncryptedSharedPreferences.create(...)
```

---

## 6. RESOURCE MANAGEMENT ISSUES

### 6.1 File Resource Not Closed in Helper.kt
**Location:** Helper.kt:16-20
**Severity:** Medium
**Category:** Resource Management - Leaks
**Description:**
BufferedReader and InputStreamReader not explicitly closed.

**Current Code:**
```kotlin
fun loadJsonArray(context: Context, resource: Int, jsonName: String): JSONArray? {
    val builder = StringBuilder()
    val `in` = context.resources.openRawResource(resource)
    val reader = BufferedReader(InputStreamReader(`in`))
    var line: String?
    try {
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
```

**Recommended Fix:**
```kotlin
fun loadJsonArray(context: Context, resource: Int, jsonName: String): JSONArray? {
    return try {
        context.resources.openRawResource(resource).bufferedReader().use { reader ->
            val json = JSONObject(reader.readText())
            json.getJSONArray(jsonName)
        }
    } catch (e: IOException) {
        Timber.e(e, "Failed to load JSON")
        null
    } catch (e: JSONException) {
        Timber.e(e, "Failed to parse JSON")
        null
    }
}
```

---

### 6.2 SimpleDateFormat Not Thread-Safe
**Location:** DateUtils.kt:9-14
**Severity:** High
**Category:** Resource Management - Thread Safety
**Description:**
SimpleDateFormat is not thread-safe but used as public static objects. Can cause crashes in concurrent access.

**Current Code:**
```kotlin
object DateUtils {
    val formatDate1 = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
    val formatDate2 = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    // ...
}
```

**Recommended Fix:**
```kotlin
object DateUtils {
    private fun createFormatter(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.ENGLISH)
    }
    
    fun Long.formatDateTo(format: String = "EEE, dd MMM yyyy"): String {
        return createFormatter(format).format(Date(this))
    }
}
```

---

### 6.3 Unused Imports Not Cleaned
**Location:** Various screen files
**Severity:** Low
**Category:** Code Organization
**Description:**
Some screens import unused composables or utilities.

**Recommended Fix:**
Run lint and remove unused imports regularly.

---

## 7. ADDITIONAL OBSERVATIONS

### 7.1 Database Version Not Updated
**Location:** CashFlowDatabase.kt:31
**Severity:** Medium
**Category:** Database Management
**Description:**
Database version is 1 and never updated. Adding new tables or modifying schema requires migration strategy.

---

### 7.2 Missing Integration Tests
**Location:** Throughout codebase
**Severity:** Medium
**Category:** Testing
**Description:**
ExampleInstrumentedTest exists but is empty. No UI or DAO tests found.

---

### 7.3 Timber Not Used Consistently
**Location:** Various files
**Severity:** Low
**Category:** Logging
**Description:**
Some files use `e.printStackTrace()` instead of Timber for logging.

---

### 7.4 Inconsistent Use of Flow vs StateFlow
**Location:** Various ViewModels
**Severity:** Low
**Category:** Architecture
**Description:**
Some ViewModels use Flow+State combinations while others use StateFlow directly. Could be more consistent.

---

## SUMMARY OF CRITICAL ISSUES

1. **Database Migration** - fallbackToDestructiveMigration() will delete all data on schema changes
2. **Missing Foreign Keys** - Account entity lacks FK to AccountGroup
3. **Unencrypted Passcode** - Sensitive data stored in plain text
4. **Hardcoded Magic Numbers** - Category/Account IDs hardcoded throughout
5. **Error Handling Missing** - Many operations silently fail
6. **Unsafe Type Conversions** - Multiple .toDouble()/.toInt() without null safety
7. **SimpleDateFormat Thread Safety** - Not thread-safe but used as static objects

## RECOMMENDATIONS PRIORITY

**Immediate (Critical):**
- Fix database migration strategy
- Add foreign key constraints
- Encrypt sensitive data in DataStore
- Add error handling to DB operations

**High:**
- Remove hardcoded IDs, use constants
- Fix unsafe type conversions
- Add SimpleDateFormat thread safety
- Implement proper error state in ViewModels

**Medium:**
- Add suspend modifiers to DAO operations
- Implement loading states in UI
- Fix LaunchedEffect dependencies
- Add input validation

**Low:**
- Refactor large composable functions
- Improve code organization
- Add ProGuard rules
- Clean up imports

