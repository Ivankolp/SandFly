package com.google.android.accessibility.talkback.preference.base;

import com.google.android.accessibility.talkback.R;

/** Smart tools: image description, macros, scripts, notifications, braille. */
public class SmartToolsFragment extends AdvancedSettingFragment {
  public SmartToolsFragment() {
    super(R.xml.smart_tools_preferences, R.string.title_pref_category_tools);
  }
}
