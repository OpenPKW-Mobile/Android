package pl.openpkw.openpkwmobile.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.openpkw.openpkwmobile.models.Protocol;
import pl.openpkw.openpkwmobile.models.User;

/**
 * Created by michalu on 18.05.15.
 */
public class OfflineStorage {
    private static final String USERS_FILE = "users";
    private static final String PROTOCOLS = "protocols";
    private static final String PROTOCOLS_TO_SEND = "protocols_to_send";
    private static final String LAST_LOGGED_IN_USER = "loggedin";

    public static void setLoggedInUser(User user, Context ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(USERS_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(LAST_LOGGED_IN_USER, new Gson().toJson(user, User.class));
        editor.commit();
    }

    public static User getLastLoggedUser(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(USERS_FILE, Context.MODE_PRIVATE);
        String userString = prefs.getString(LAST_LOGGED_IN_USER, null);
        User user = null;
        try {
            user = new Gson().fromJson(userString, User.class);
        } catch (JsonSyntaxException e) {

        }
        return user;
    }

    public static List<Protocol> getSavedProtocolsForUpload(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PROTOCOLS, Context.MODE_PRIVATE);
        String protocolsString = prefs.getString(PROTOCOLS_TO_SEND, null);
        List<Protocol> protocols = null;
        Type listType = new TypeToken<List<Protocol>>() {
        }.getType();
        try {
            protocols = new Gson().fromJson(protocolsString, listType);
        } catch (JsonSyntaxException e) {

        }
        return protocols;
    }

    public static void addProtocolForUpload(Context ctx, Protocol protocol) {
        SharedPreferences prefs = ctx.getSharedPreferences(PROTOCOLS, Context.MODE_PRIVATE);
        String protocolsString = prefs.getString(PROTOCOLS_TO_SEND, null);
        List<Protocol> protocols = null;
        Type listType = new TypeToken<List<Protocol>>() {
        }.getType();
        try {
            protocols = new Gson().fromJson(protocolsString, listType);

        } catch (JsonSyntaxException e) {

        }
        if (protocols == null)
            protocols = new ArrayList<>();
        protocols.add(protocol);
        protocolsString = new Gson().toJson(protocols, listType);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROTOCOLS_TO_SEND, protocolsString);
        editor.commit();
    }

    public static void removeProtocol(Context ctx, Protocol toRemove) {
        SharedPreferences prefs = ctx.getSharedPreferences(PROTOCOLS, Context.MODE_PRIVATE);
        String protocolsString = prefs.getString(PROTOCOLS_TO_SEND, null);
        List<Protocol> protocols = null;
        Type listType = new TypeToken<List<Protocol>>() {
        }.getType();
        try {
            protocols = new Gson().fromJson(protocolsString, listType);

        } catch (JsonSyntaxException e) {

        }
        if (protocols == null)
            return;
        for (Protocol p : protocols) {
            if (p.equals(toRemove)) {
                protocols.remove(p);
            }
        }
        protocolsString = new Gson().toJson(protocols, listType);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROTOCOLS_TO_SEND, protocolsString);
        editor.commit();
    }

    public static boolean isTokenValid(String sessionTimeout, Context ctx) {
        long sessionLong = Long.parseLong(sessionTimeout);
        long now = Calendar.getInstance().getTimeInMillis();
        if (now > sessionLong)
            return false;
        else
            return true;
    }
}
