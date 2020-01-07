package com.csh.studycollection.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences操作类
 */
public final class SharedPrefHelper {

    private static SharedPreferences mSharePref;

    public static void init(Context context) {
        mSharePref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static void putInt(String key, int value) {
        mSharePref.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        return mSharePref.getInt(key, defaultValue);
    }

    public static void putFloat(String key, float value) {
        mSharePref.edit().putFloat(key, value).apply();
    }

    public static float getFloat(String key, float defaultValue) {
        return mSharePref.getFloat(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        mSharePref.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return mSharePref.getBoolean(key, defaultValue);
    }

    public static void putString(String key, String value) {
        mSharePref.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return mSharePref.getString(key, defaultValue);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String getUserVid() {
        return mSharePref.getString("vid", "");
    }

    public static String getProjetId() {
        return mSharePref.getString("projectId", "");
    }
    public static String getProjectName() {
        return mSharePref.getString("projectName", "");
    }

    public static String getUserNameWX() {
        return mSharePref.getString("userNameWX", "");
    }
    public static String getWXHeaderIcon() {
        return mSharePref.getString("headImgUrlWX", "");
    }

    public static String getRealName() {
        return mSharePref.getString("realName", "");
    }

    public static String getMoblie() {
        return mSharePref.getString("mobile", "");
    }

    public static String getEmail() {
        return mSharePref.getString("email", "");
    }

    public static String getT() {
        return mSharePref.getString("t", "");
    }

    public static String getOrgId() {
        return mSharePref.getString("orgId", "");
    }

    public static String getAppPermissions() {
        return mSharePref.getString("appPermissions", "");
    }

    public static String getSites() {
        return mSharePref.getString("sites", "");
    }

    public static String getAddSubjectSites() {
        return mSharePref.getString("add_subject_sites", "");
    }

    public static String getHomeSiteId() {
        return mSharePref.getString("home_siteId", "");
    }
    public static String getProjectLogo() {
        return mSharePref.getString("ProjectLogo", "");
    }
    public static void setT(String token) {
        mSharePref.edit().putString("t", token).apply();
    }
    public static void setProjectLogo(String projLogo) {
        mSharePref.edit().putString("ProjectLogo", projLogo).apply();
    }

    public static void setUserVid(String vid) {
        mSharePref.edit().putString("vid", vid).apply();
    }

    public static void setProjetId(String projectId) {
        mSharePref.edit().putString("projectId", projectId).apply();
    }
    public static void setProjetName(String projName) {
        mSharePref.edit().putString("projectName", projName).apply();
    }

    public static void setUserNameWX(String userNameWX) {
        mSharePref.edit().putString("userNameWX", userNameWX).apply();
    }

    public static void setRealName(String realName) {
        mSharePref.edit().putString("realName", realName).apply();
    }

    public static void setMobile(String mobile) {
        mSharePref.edit().putString("mobile", mobile).apply();
    }

    public static void setEmail(String email) {
        mSharePref.edit().putString("email", email).apply();
    }
    public static void setOrgId(String orgId) {
        mSharePref.edit().putString("orgId", orgId).apply();
    }
    public static void setAppPermissions(String appPermissions) {
        mSharePref.edit().putString("appPermissions", appPermissions).apply();
    }
    public static void setSites(String sites) {
        mSharePref.edit().putString("sites", sites).apply();
    }
    public static void setHomeSite(String siteId) {
        mSharePref.edit().putString("home_siteId", siteId).apply();
    }

    public static void setAddSubjectSites(String sites) {
        mSharePref.edit().putString("add_subject_sites", sites).apply();
    }

    public static void setTextFont(float size) {
        mSharePref.edit().putFloat("app_text_font", size).apply();
    }
    public static float getTextFont() {
        return mSharePref.getFloat("app_text_font", 1.0f);
    }

    public static void setProjectListSelectedTags(int tags) {
        mSharePref.edit().putInt("project_list_tags", tags).apply();
    }
    public static int getProjectListSelectedTags() {
        return mSharePref.getInt("project_list_tags", 2);
    }

    public static void setAppOrgLogo(String appLogoId) {
        mSharePref.edit().putString("appOrgLogoId", appLogoId).apply();
    }
    public static String getAppOrgLogo() {
        return mSharePref.getString("appOrgLogoId", "");
    }

    public static void setLanguage(String language) {
        mSharePref.edit().putString("language", language).apply();
    }
    public static String getLanguage() {
        return mSharePref.getString("language", "auto");
    }

    public static void setNode(String node) {
        mSharePref.edit().putString("node", node).apply();
    }
    public static String getNode() {
        return mSharePref.getString("node", "default");
    }
}
