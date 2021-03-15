package co.thanker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;

import co.thanker.PremiumActivityNo;
import co.thanker.utils.Constants;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains helper static methods for dealing with the Payments API.
 *
 * <p>Many of the parameters used in the code are optional and are set here merely to call out their
 * existence. Please consult the documentation to learn more and feel free to remove ones not
 * relevant to your implementation.
 */
public class PaymentsUtils {

    public static final BigDecimal CENTS_IN_A_UNIT = new BigDecimal(100d);
    private static final String PAYPAL_ID = "AdJMd3kfDjucSGXwfQMhc1HLNTYr5ih0SAcNXvARGFO5l1gv5wdLRgxhfpg6MN3JKhUecnD28hemRMIq";
    private static final String PAYPAL_ID_SANDBOX = "AUbsCpJu9zIZ-qM0g8wjgdejDpKddr6FlAfxEARt4ZvfNI8y9kdJ3MAa0B57YiVC2npH1efNIxkanuZJ";

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    /**
     * Creates an instance of {@link PaymentsClient} for use in an {@link Activity} using the
     * environment and theme set in {@link Constants}.
     *
     * @param activity is the caller's activity.
     */
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     * <p>The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     * <p>TODO: Check with your gateway on the parameters to pass and modify them in Constants.java.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see <a href=
     * "https://developers.google.com/pay/api/android/reference/object#PaymentMethodTokenizationSpecification">PaymentMethodTokenizationSpecification</a>
     */
    /*private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {
                {
                    put("gateway", "example");
                    put("gatewayMerchantId", PAYPAL_ID);
                }
            });
        }};
    }*/

    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject(){
                {
                    put("gateway", "stripe");
                    put("stripe:version", "2018-10-31");
                    put("stripe:publishableKey", PremiumActivityNo.STRIPE_KEY_TEST);
                }
                    });
                    /*new JSONObject() {
                {
                    put("gateway", "example");
                    put("gatewayMerchantId", "exampleGatewayMerchantId");
                }
            });*/
        }};
    }

    /**
     * {@code DIRECT} Integration: Decrypt a response directly on your servers. This configuration has
     * additional data security requirements from Google and additional PCI DSS compliance complexity.
     *
     * <p>Please refer to the documentation for more information about {@code DIRECT} integration. The
     * type of integration you use depends on your payment processor.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethodTokenizationSpecification">PaymentMethodTokenizationSpecification</a>
     */
    private static JSONObject getDirectTokenizationSpecification()
            throws JSONException, RuntimeException {
        if (Constants.DIRECT_TOKENIZATION_PARAMETERS.isEmpty()
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty()
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == null
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == "REPLACE_ME") {
            throw new RuntimeException(
                    "Please edit the Constants.java file to add protocol version & public key.");
        }
        JSONObject tokenizationSpecification = new JSONObject();

        tokenizationSpecification.put("type", "DIRECT");
        JSONObject parameters = new JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS);
        tokenizationSpecification.put("parameters", parameters);
        //tokenizationSpecification.put("type", "PAYPAL")

        return tokenizationSpecification;
    }

    /**
     * Card networks supported by your app and your gateway.
     *
     * <p>TODO: Confirm card networks supported by your app and gateway & update in Constants.java.
     *
     * @return Allowed card networks
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(Constants.SUPPORTED_NETWORKS);
    }

    /**
     * Card authentication methods supported by your app and your gateway.
     *
     * <p>TODO: Confirm your processor supports Android device tokens on your supported card networks
     * and make updates in Constants.java.
     *
     * @return Allowed card authentication methods.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray(Constants.SUPPORTED_METHODS);
    }

    /**
     * Describe your app's support for the CARD payment method.
     *
     * <p>The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        //parameters.put("billingAddressRequired", true);

        /*JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);*/

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardMethod = getBaseCardPaymentMethod();
        cardMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardMethod;
    }

    private static JSONObject getPaypalMethod() throws JSONException {
        JSONObject paypalMethod = new JSONObject();
        paypalMethod.put("type", "PAYPAL");

        paypalMethod.put("parameters", new JSONObject(){
                    {
                        put("purchase_context", new JSONObject(){
                            {
                                put("purchase_units", new JSONArray()
                                        .put(new JSONObject(){
                                            {
                                                put("payee", new JSONObject(){
                                                    {
                                                        put("merchant_id", PAYPAL_ID_SANDBOX);
                                                    }
                                                });
                                            }

                                        })
                                );
                            }
                            });
                        }
                    });

        paypalMethod.put("tokenizationSpecification", new JSONObject() {
                        {
                            put("type", "DIRECT");
                        }
                    });


        return paypalMethod;
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#IsReadyToPayRequest">IsReadyToPayRequest</a>
     */
    @SuppressLint("NewApi")
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod()));
            /*.put(getPaypalMethod()))*/; //put(getBaseCardPaymentMethod())

            return Optional.of(isReadyToPayRequest);

        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#TransactionInfo">TransactionInfo</a>
     */
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE);
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#MerchantInfo">MerchantInfo</a>
     */
    private static JSONObject getMerchantInfo() throws JSONException {

        JSONObject merchantParameters = new JSONObject();
        merchantParameters.put("merchantId", "BCR2DN6TVPF2VHYL");
        merchantParameters.put("merchantName", "Thanker");

        return merchantParameters;
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentDataRequest">PaymentDataRequest</a>
     */
    @SuppressLint("NewApi")
    public static Optional<JSONObject> getPaymentDataRequest(long priceCents) {

        final String price = PaymentsUtils.centsToString(priceCents);

        try {
            JSONObject paymentDataRequest = PaymentsUtils.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtils.getCardPaymentMethod()));
                //.put(getPaypalMethod()));
            paymentDataRequest.put("transactionInfo", PaymentsUtils.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", PaymentsUtils.getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
            /*paymentDataRequest.put("shippingAddressRequired", true);

            JSONObject shippingAddressParameters = new JSONObject();
            shippingAddressParameters.put("phoneNumberRequired", false);

            JSONArray allowedCountryCodes = new JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES);

            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);*/
            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**

     *
     * @param cents value of the price in cents.
     */
    public static String centsToString(long cents) {
        return new BigDecimal(cents)
                .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN)
                .toString();
    }
}