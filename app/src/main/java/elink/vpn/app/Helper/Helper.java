package elink.vpn.app.Helper;


import android.content.Context;
import android.widget.Toast;

public class Helper {
    public static final String ITEM_ID_LIST = "ITEM_ID_LIST";
    public static final String ITEM_ONE_ID = "productone";
    public static final String ITEM_TWO_ID = "producttwo";
    public static final String ITEM_THREE_ID = "productthree";
    public static final int RESPONSE_CODE = 1001;
    public static final String SHARED_PREF = "shared_pref";
    public static final String DEVELOPER_PAYLOAD = "developer_payload";
    public static final String PURCHASE_TOKEN = "purchase_token";
    public static void displayMessage(Context context, String message){
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}