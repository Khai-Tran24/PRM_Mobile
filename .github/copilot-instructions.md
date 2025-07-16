# Copilot Instructions for SaleHunter Project

## Project Overview
SaleHunter is a multi-platform price comparison app with:
- **Android Frontend** (`FE-SaleHunter/`) - Java/Android with MVVM architecture
- **.NET Backend** (`BE-SaleHunter/`) - Clean Architecture with Authentication APIs
- **React Web Frontend** (`PRM392-Web-SaleHunter/`) - TypeScript/Vite/Tailwind

## Architecture Patterns

### Android Frontend (Primary Focus)
**MVVM + Repository Pattern** with RxJava and Retrofit:
```
view/ (Fragments/Activities) → viewmodel/ → data/Repository → data/remote/RetrofitInterface
```

**Key Components:**
- `Repository.java` - Central data layer using RxJava with LiveDataReactiveStreams
- `RetrofitInterface.java` - API definitions with observables
- ViewModels return `LiveData<Response<T>>` for UI observation
- Authentication uses Bearer tokens via `UserAccountManager`

### Critical Patterns

**1. LiveData Null Safety Pattern:**
```java
// Always check for null before returning LiveData to prevent crashes
public LiveData<Response<ProductsResponseModel>> getProducts(){
    if (products == null) {
        products = repository.searchProducts(token, "", null, null, null, null);
    }
    return products;
}

// Always check null in observer removal
public void removeObserver(LifecycleOwner lifecycleOwner){
    if (products != null) {
        products.removeObservers(lifecycleOwner);
    }
}
```

**2. Model Conversion Pattern:**
```java
// Bridge old/new API differences with conversion methods
public class SignInModel {
    public LoginRequestModel toLoginRequestModel() {
        LoginRequestModel model = new LoginRequestModel();
        model.setEmail(this.email);
        model.setPassword(this.password);
        return model;
    }
}
```

**3. API Structure:**
- Authentication: `/api/Auth/login`, `/api/Auth/register` 
- Products: `/api/Product/search?query=...&storeId=...`
- User data: `/api/User/profile`, `/api/User/favorites`
- All require `Authorization: Bearer <token>` header

## Development Workflows

### Build Commands
```bash
# Android (in FE-SaleHunter/)
./gradlew compileDebugJavaWithJavac  # Compilation check only
./gradlew assembleDebug              # Full APK build
./gradlew build                      # Full build with lint

# Backend (in BE-SaleHunter/)
dotnet build BE-SaleHunter.sln
dotnet run --project BE-SaleHunter/
```

### Common Issue Patterns

**1. Type Mismatches:** Backend uses `Double` for prices, Android models use `long` - cast with `(double)`

**2. Response Structure:** New auth responses have nested data:
```java
// OLD: response.body().getUser()
// NEW: response.body().getData().getUser()
```

**3. Model Updates:** When updating models, always check:
- Adapter classes using old method names
- Fragment files calling model methods  
- ViewModel return types matching new response models

## Integration Points

### API Synchronization
Reference `API_MIGRATION_GUIDE.md` for backend↔frontend model mappings.

**Authentication Flow:**
1. Login/Register → Auth endpoints return `LoginResponseModel`/`RegisterResponseModel`
2. Extract token from `response.headers().get("Authorization")`
3. Store via `UserAccountManager.signIn()`
4. Use token in subsequent API calls

### Navigation Architecture
- `MainActivity` with bottom navigation
- Fragment-based with Navigation Component
- Home uses nested NavHostFragment with `homepage_nav.xml`

## File Organization
- `model/auth/` - New authentication models
- `model/` - Core data models (User, Product, Store)
- `data/` - Repository pattern implementation
- `viewmodel/fragment/` - Fragment-specific ViewModels
- `view/fragment/` - UI fragments
- `util/` - Shared utilities (UserAccountManager, SharedPrefManager)

## Testing & Debugging
- Use `./gradlew compileDebugJavaWithJavac` for quick compilation checks
- Check `COMPILATION_FIXES_PROGRESS.md` for known issues and fixes
- LiveData crashes usually indicate null returns from ViewModel methods
- Fragment inflation errors often trace to ViewModel initialization issues
