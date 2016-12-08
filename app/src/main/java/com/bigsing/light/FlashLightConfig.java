package com.bigsing.light;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sing on 2016/9/14.
 */
public class FlashLightConfig {
    private SharedPreferences m_sp;
    private SharedPreferences.Editor m_editor;

    public FlashLightConfig(Context paramContext, String paramString) {
        m_sp = paramContext.getSharedPreferences(paramString, 0);
        m_editor = m_sp.edit();
    }

    public String getGoogleId(String paramString) {
        return m_sp.getString("googleid", paramString);
    }

    public void setPageIndex(boolean paramBoolean) {
        m_editor.putBoolean("pageindex", paramBoolean);
        m_editor.commit();
    }

    public boolean getPageIndex() {
        return this.m_sp.getBoolean("pageindex", false);
    }

    public void b(String paramString) {
        m_editor.putString("googleid", paramString);
        m_editor.commit();
    }

    public void b(boolean paramBoolean) {
        m_editor.putBoolean("qickstart", paramBoolean);
        m_editor.commit();
    }

    public boolean getQickStart() {
        return m_sp.getBoolean("qickstart", true);
    }
}
