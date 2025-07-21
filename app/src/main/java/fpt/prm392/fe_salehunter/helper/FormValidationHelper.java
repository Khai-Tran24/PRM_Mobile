package fpt.prm392.fe_salehunter.helper;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import fpt.prm392.fe_salehunter.R;

/**
 * Helper class for form validation across the app
 */
public class FormValidationHelper {
    
    /**
     * Validate required text field
     */
    public static boolean validateRequiredField(Context context, TextInputLayout textInputLayout, String fieldName) {
        if (textInputLayout.getError() != null || 
            textInputLayout.getEditText() == null ||
            textInputLayout.getEditText().getText().toString().trim().isEmpty()) {
            
            showFieldError(context, textInputLayout, fieldName + " is required");
            return false;
        }
        return true;
    }
    
    /**
     * Validate email format
     */
    public static boolean validateEmailFormat(Context context, TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText() == null) return false;
        
        String email = textInputLayout.getEditText().getText().toString().trim();
        if (email.isEmpty()) return true; // Allow empty if not required
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showFieldError(context, textInputLayout, "Please enter a valid email address");
            return false;
        }
        return true;
    }
    
    /**
     * Validate URL format
     */
    public static boolean validateUrlFormat(Context context, TextInputLayout textInputLayout, String fieldName) {
        if (textInputLayout.getEditText() == null) return false;
        
        String url = textInputLayout.getEditText().getText().toString().trim();
        if (url.isEmpty()) return true; // Allow empty if not required
        
        if (!android.util.Patterns.WEB_URL.matcher(url).matches()) {
            showFieldError(context, textInputLayout, "Please enter a valid " + fieldName + " URL");
            return false;
        }
        return true;
    }
    
    /**
     * Validate phone number format
     */
    public static boolean validatePhoneFormat(Context context, TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText() == null) return false;
        
        String phone = textInputLayout.getEditText().getText().toString().trim();
        if (phone.isEmpty()) return true; // Allow empty if not required
        
        if (!android.util.Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            showFieldError(context, textInputLayout, "Please enter a valid phone number");
            return false;
        }
        return true;
    }
    
    /**
     * Validate minimum length
     */
    public static boolean validateMinLength(Context context, TextInputLayout textInputLayout, 
                                          String fieldName, int minLength) {
        if (textInputLayout.getEditText() == null) return false;
        
        String text = textInputLayout.getEditText().getText().toString().trim();
        if (text.length() < minLength) {
            showFieldError(context, textInputLayout, 
                fieldName + " must be at least " + minLength + " characters long");
            return false;
        }
        return true;
    }
    
    /**
     * Validate maximum length
     */
    public static boolean validateMaxLength(Context context, TextInputLayout textInputLayout, 
                                          String fieldName, int maxLength) {
        if (textInputLayout.getEditText() == null) return false;
        
        String text = textInputLayout.getEditText().getText().toString().trim();
        if (text.length() > maxLength) {
            showFieldError(context, textInputLayout, 
                fieldName + " must not exceed " + maxLength + " characters");
            return false;
        }
        return true;
    }
    
    /**
     * Validate numeric input
     */
    public static boolean validateNumeric(Context context, TextInputLayout textInputLayout, 
                                        String fieldName, boolean allowDecimal) {
        if (textInputLayout.getEditText() == null) return false;
        
        String text = textInputLayout.getEditText().getText().toString().trim();
        if (text.isEmpty()) return true; // Allow empty if not required
        
        try {
            if (allowDecimal) {
                Double.parseDouble(text);
            } else {
                Integer.parseInt(text);
            }
            return true;
        } catch (NumberFormatException e) {
            showFieldError(context, textInputLayout, 
                fieldName + " must be a valid " + (allowDecimal ? "number" : "integer"));
            return false;
        }
    }
    
    /**
     * Validate price format
     */
    public static boolean validatePrice(Context context, TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText() == null) return false;
        
        String priceText = textInputLayout.getEditText().getText().toString().trim();
        if (priceText.isEmpty()) {
            showFieldError(context, textInputLayout, "Price is required");
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showFieldError(context, textInputLayout, "Price cannot be negative");
                return false;
            }
            if (price > 999999999) {
                showFieldError(context, textInputLayout, "Price is too large");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showFieldError(context, textInputLayout, "Please enter a valid price");
            return false;
        }
    }
    
    /**
     * Show field error with animation
     */
    private static void showFieldError(Context context, TextInputLayout textInputLayout, String message) {
        textInputLayout.setError(message);
        textInputLayout.requestFocus();
        textInputLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fieldmissing));
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Clear field error
     */
    public static void clearFieldError(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }
    
    /**
     * Validate image selection
     */
    public static boolean validateImageRequired(Context context, Object imageData, String fieldName) {
        if (imageData == null) {
            Toast.makeText(context, fieldName + " is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
