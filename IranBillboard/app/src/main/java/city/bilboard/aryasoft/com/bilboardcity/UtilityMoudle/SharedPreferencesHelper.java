package city.bilboard.aryasoft.com.bilboardcity.UtilityMoudle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper
{
    private static SharedPreferences preferences = null;
    private static SharedPreferences.Editor editor = null;

    public static void InitPreferences(Context AppContext)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(AppContext);
        editor = preferences.edit();
    }

    private static void SaveChanges()
    {
        editor.apply();
        editor.commit();
    }

    public static String ReadString(String KeyName)
    {
        return preferences.getString(KeyName, "0");
    }

    public static void WriteString(String KeyName, String Value)
    {
        editor.putString(KeyName, Value);
        SaveChanges();
    }

    public static int ReadInt(String KeyName)
    {
        return preferences.getInt(KeyName, -1);
    }

    public static void ClearPreferences()
    {
        editor.clear();
        editor.commit();
    }
    public static void ClearPreferences(String KeyName)
    {
        editor.remove(KeyName);
        editor.commit();
    }
    public static void WriteInt(String KeyName, int Value)
    {
        editor.putInt(KeyName, Value);
        SaveChanges();
    }

    public static long ReadLong(String KeyName)
    {
        return preferences.getLong(KeyName, -1);
    }

    public static void WriteLong(String KeyName, long Value)
    {
        editor.putLong(KeyName, Value);
        SaveChanges();
    }

    public static float ReadFloat(String KeyName)
    {
        return preferences.getFloat(KeyName, -1);
    }

    public static void WriteFloat(String KeyName, float Value)
    {
        editor.putFloat(KeyName, Value);
        SaveChanges();
    }
}
