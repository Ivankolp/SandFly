package com.google.android.accessibility.talkback.preference.base;

import com.google.android.accessibility.talkback.R;

/** System settings: display, backup, shizuku, updates, help, developer. */
public class SystemSettingsFragment extends AdvancedSettingFragment {
  public SystemSettingsFragment() {
    super(R.xml.system_settings_preferences, R.string.title_pref_category_system);
  }
}
