package fpt.prm392.fe_salehunter.data.remote;

import fpt.prm392.fe_salehunter.data.remote.interfaces.AuthInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.BarcodeInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.ChatInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.PaymentInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.ProductInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.StoreInterface;
import fpt.prm392.fe_salehunter.data.remote.interfaces.UserInterface;

/**
 * Main RetrofitInterface that combines all feature-specific interfaces
 * This approach provides better organization and maintainability
 */
public interface RetrofitInterface extends 
    AuthInterface, 
    UserInterface, 
    StoreInterface, 
    ProductInterface, 
    PaymentInterface, 
    BarcodeInterface,
    ChatInterface {
    
    // All methods are inherited from the feature-specific interfaces
    // This composite pattern provides a single point of access while maintaining modularity
}
