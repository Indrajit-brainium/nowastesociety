package com.example.nowastesociety.allurl;

public class AllUrl {


    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static String KEY_PASSWORD = null;
    public static String USER_NAME = "USER_NAME";
    public static String baseUrl;
    public static String RegistrationUrl;
    public static String loginUrl;
    public static String OTPurl;
    public static String Editprofileurl;
    public static String Changepasswordurl;
    public static String Forgotpasswordurl;
    public static String Resetpasswordurl;
    public static String Imageuploadurl;
    public static String ResendOTP;
    public static String DashboardResturants;
    public static String Logout;
    public static String AddressList;
    public static String EditAddress;
    public static String AddAddress;
    public static String DeleteAddress;
    public static String ResturantDetails;
    public static String PostOrder;
    public static String ChangeEmail;
    public static String ResetEmail;
    public static String CardList;
    public static String AddCard;
    public static String FetchCart;
    public static String AddToCart;
    public static String RemoveToCart;
    public static String UpdateCart;
    public static String RemovePreviousWholeCart;
    public static String AddtoFav;
    public static String FavList;
    public static String unFav;


















    // https://researchcrm.aro-crm.com/api/crmlogin.php?url=https://researchcrm.aro-crm.com&username=admin&password=admin123
    static {
        baseUrl = "https://nodeserver.brainiuminfotech.com:2100/api/";
        RegistrationUrl = baseUrl + "customer/registration";
        OTPurl = baseUrl + "customer/otpVerification";
        loginUrl = baseUrl + "customer/login";
        Editprofileurl = baseUrl + "customer/editProfile";
        Changepasswordurl = baseUrl+"customer/changePassword";
        Forgotpasswordurl = baseUrl+"customer/forgotPassword";
        Resetpasswordurl = baseUrl+"customer/resetPassword";
        Imageuploadurl = baseUrl+"customer/profileImageUpload";
        ResendOTP = baseUrl+"customer/resendOtp";
        DashboardResturants = baseUrl+"customer/dashboard";
        Logout = baseUrl+"customer/logout";
        AddressList = baseUrl+"customer/addressList";
        EditAddress = baseUrl+"customer/editAddress";
        AddAddress = baseUrl+"customer/addAddress";
        DeleteAddress = baseUrl+"customer/deleteCustomerAddress";
        ResturantDetails = baseUrl+"customer/restaurantDetail";
        PostOrder = baseUrl+"customer/postOrder";
        ChangeEmail = baseUrl+"customer/changeEmail";
        ResetEmail = baseUrl+"customer/resetEmail";
        CardList = baseUrl+"customer/cardList";
        AddCard = baseUrl+"customer/addPaymentDetails";
        FetchCart = baseUrl+"customer/fetchCart";
        AddToCart = baseUrl+"customer/addToCart";
        RemoveToCart = baseUrl+"customer/removeCartItem";
        UpdateCart = baseUrl+"customer/updateCartItem";
        RemovePreviousWholeCart = baseUrl+"customer/removePreviousWholeCart";
        AddtoFav = baseUrl+"customer/markAsFavorite";
        unFav = baseUrl+"customer/markAsUnFavorite";
        FavList = baseUrl+"customer/favoriteRestaurantsList?latitude=22.599331&longitude=88.444899";



        USER_NAME = "user_name";
        KEY_PASSWORD = "password";
    }
}
