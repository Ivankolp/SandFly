/*
 * Copyright 2026 SandFly / TurboFly contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.accessibility.utils.ocr;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eliminates ML Kit Latin OCR hallucinations on Cyrillic (Russian/Ukrainian/Belarusian) text.
 * ML Kit text recognition on-device has no Cyrillic model and runs the Latin recognizer,
 * causing Cyrillic letters to be hallucinated as visual Latin lookalikes (e.g. "Nouck" -> "Поиск",
 * "Hazag" -> "Назад", "OmmeHa" -> "Отмена", "CoxpaHumb" -> "Сохранить", "Bblxog" -> "Выход").
 */
public final class CyrillicOcrFixer {

  private static final Map<String, String> COMMON_UI_WORDS = new HashMap<>();

  static {
    COMMON_UI_WORDS.put("nouck", "Поиск");
    COMMON_UI_WORDS.put("houck", "Поиск");
    COMMON_UI_WORDS.put("hazag", "Назад");
    COMMON_UI_WORDS.put("hazad", "Назад");
    COMMON_UI_WORDS.put("hasaq", "Назад");
    COMMON_UI_WORDS.put("ommeha", "Отмена");
    COMMON_UI_WORDS.put("ommehа", "Отмена");
    COMMON_UI_WORDS.put("ommena", "Отмена");
    COMMON_UI_WORDS.put("ommeh", "Отмена");
    COMMON_UI_WORDS.put("coxpahumb", "Сохранить");
    COMMON_UI_WORDS.put("coxpaнить", "Сохранить");
    COMMON_UI_WORDS.put("coxpahumь", "Сохранить");
    COMMON_UI_WORDS.put("coxpahumb", "Сохранить");
    COMMON_UI_WORDS.put("bblxog", "Выход");
    COMMON_UI_WORDS.put("bblxод", "Выход");
    COMMON_UI_WORDS.put("bbixog", "Выход");
    COMMON_UI_WORDS.put("boumu", "Войти");
    COMMON_UI_WORDS.put("boúmu", "Войти");
    COMMON_UI_WORDS.put("hampouku", "Настройки");
    COMMON_UI_WORDS.put("hacmpouku", "Настройки");
    COMMON_UI_WORDS.put("hacmpoйku", "Настройки");
    COMMON_UI_WORDS.put("hacmpouku", "Настройки");
    COMMON_UI_WORDS.put("mehio", "Меню");
    COMMON_UI_WORDS.put("meню", "Меню");
    COMMON_UI_WORDS.put("mehю", "Меню");
    COMMON_UI_WORDS.put("ganee", "Далее");
    COMMON_UI_WORDS.put("danee", "Далее");
    COMMON_UI_WORDS.put("tomobo", "Готово");
    COMMON_UI_WORDS.put("romobo", "Готово");
    COMMON_UI_WORDS.put("3akpbimb", "Закрыть");
    COMMON_UI_WORDS.put("3akpumb", "Закрыть");
    COMMON_UI_WORDS.put("3akpыть", "Закрыть");
    COMMON_UI_WORDS.put("3akpbimь", "Закрыть");
    COMMON_UI_WORDS.put("zakpbimb", "Закрыть");
    COMMON_UI_WORDS.put("zakpumb", "Закрыть");
    COMMON_UI_WORDS.put("nogenuimscq", "Поделиться");
    COMMON_UI_WORDS.put("nogenumbcq", "Поделиться");
    COMMON_UI_WORDS.put("nogenumbся", "Поделиться");
    COMMON_UI_WORDS.put("nogenumься", "Поделиться");
    COMMON_UI_WORDS.put("yganumb", "Удалить");
    COMMON_UI_WORDS.put("ygaлumb", "Удалить");
    COMMON_UI_WORDS.put("yganumь", "Удалить");
    COMMON_UI_WORDS.put("o4ucmumb", "Очистить");
    COMMON_UI_WORDS.put("oчucmumb", "Очистить");
    COMMON_UI_WORDS.put("cnucok", "Список");
    COMMON_UI_WORDS.put("coobwehuq", "Сообщения");
    COMMON_UI_WORDS.put("cooбwehuq", "Сообщения");
    COMMON_UI_WORDS.put("ckayamb", "Скачать");
    COMMON_UI_WORDS.put("cka4amb", "Скачать");
    COMMON_UI_WORDS.put("ompabumb", "Отправить");
    COMMON_UI_WORDS.put("omnpabumb", "Отправить");
    COMMON_UI_WORDS.put("dobabumb", "Добавить");
    COMMON_UI_WORDS.put("obhobumb", "Обновить");
    COMMON_UI_WORDS.put("bce", "Все");
    COMMON_UI_WORDS.put("bceгo", "Всего");
    COMMON_UI_WORDS.put("nayza", "Пауза");
    COMMON_UI_WORDS.put("cmon", "Стоп");
    COMMON_UI_WORDS.put("npogonжumb", "Продолжить");
    COMMON_UI_WORDS.put("npoдолжumb", "Продолжить");
    COMMON_UI_WORDS.put("npuнsmb", "Принять");
    COMMON_UI_WORDS.put("npuняmb", "Принять");
    COMMON_UI_WORDS.put("nponycmumb", "Пропустить");
    COMMON_UI_WORDS.put("co3gamb", "Создать");
    COMMON_UI_WORDS.put("u3mehumb", "Изменить");
    COMMON_UI_WORDS.put("kop3uha", "Корзина");
    COMMON_UI_WORDS.put("uнфopmauuq", "Информация");
    COMMON_UI_WORDS.put("cnpabka", "Справка");
    COMMON_UI_WORDS.put("noмощь", "Помощь");
    COMMON_UI_WORDS.put("noдробнее", "Подробнее");
    COMMON_UI_WORDS.put("nogpoбhee", "Подробнее");
    COMMON_UI_WORDS.put("bblбpamb", "Выбрать");
    COMMON_UI_WORDS.put("omkpbimb", "Открыть");
    COMMON_UI_WORDS.put("omkpumb", "Открыть");
  }

  private CyrillicOcrFixer() {}

  /** Checks if the user's default locale uses Cyrillic script. */
  public static boolean isCyrillicLocale() {
    String lang = Locale.getDefault().getLanguage();
    return "ru".equalsIgnoreCase(lang)
        || "uk".equalsIgnoreCase(lang)
        || "be".equalsIgnoreCase(lang)
        || "bg".equalsIgnoreCase(lang)
        || "kk".equalsIgnoreCase(lang)
        || "ky".equalsIgnoreCase(lang);
  }

  /**
   * Fixes OCR text by mapping known Latin hallucinations to proper Cyrillic text.
   */
  public static String fix(CharSequence rawText) {
    if (TextUtils.isEmpty(rawText)) {
      return "";
    }
    String text = rawText.toString().trim();
    if (!isCyrillicLocale()) {
      return text;
    }

    String[] words = text.split("\\s+");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      if (i > 0) {
        result.append(" ");
      }
      result.append(fixWord(word));
    }

    return result.toString();
  }

  private static String fixWord(String word) {
    if (TextUtils.isEmpty(word)) {
      return word;
    }

    // Strip trailing punctuation for dictionary check
    String cleanWord = word.replaceAll("[^a-zA-Zа-яА-Я0-9]", "");
    String lower = cleanWord.toLowerCase(Locale.ROOT);

    if (COMMON_UI_WORDS.containsKey(lower)) {
      String mapped = COMMON_UI_WORDS.get(lower);
      if (mapped != null) {
        // Preserve case
        if (Character.isLowerCase(cleanWord.charAt(0))) {
          return word.replace(cleanWord, mapped.toLowerCase(Locale.ROOT));
        } else {
          return word.replace(cleanWord, mapped);
        }
      }
    }

    // Check if word has mixed Cyrillic + Latin homoglyphs (e.g. Cooбщения, Coxpaнить)
    boolean hasCyrillic = containsCyrillic(word);
    boolean hasLatin = containsLatin(word);

    if (hasCyrillic && hasLatin) {
      return replaceLatinHomoglyphsWithCyrillic(word);
    }

    // Check for clear OCR artifacts: "bl" digraph representing "ы"
    if (word.contains("bl") || word.contains("bI")) {
      String fixed = word.replace("bl", "ы").replace("bI", "Ы");
      return replaceLatinHomoglyphsWithCyrillic(fixed);
    }

    return word;
  }

  private static boolean containsCyrillic(String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if ((c >= '\u0400' && c <= '\u04FF') || (c >= '\u0500' && c <= '\u052F')) {
        return true;
      }
    }
    return false;
  }

  private static boolean containsLatin(String str) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
        return true;
      }
    }
    return false;
  }

  private static String replaceLatinHomoglyphsWithCyrillic(String str) {
    StringBuilder sb = new StringBuilder(str.length());
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case 'a': sb.append('а'); break;
        case 'A': sb.append('А'); break;
        case 'B': sb.append('В'); break;
        case 'c': sb.append('с'); break;
        case 'C': sb.append('С'); break;
        case 'e': sb.append('е'); break;
        case 'E': sb.append('Е'); break;
        case 'H': sb.append('Н'); break;
        case 'i': sb.append('і'); break;
        case 'k': sb.append('к'); break;
        case 'K': sb.append('К'); break;
        case 'm': sb.append('т'); break;
        case 'M': sb.append('М'); break;
        case 'o': sb.append('о'); break;
        case 'O': sb.append('О'); break;
        case 'p': sb.append('р'); break;
        case 'P': sb.append('Р'); break;
        case 'r': sb.append('г'); break;
        case 'T': sb.append('Т'); break;
        case 'u': sb.append('и'); break;
        case 'x': sb.append('х'); break;
        case 'X': sb.append('Х'); break;
        case 'y': sb.append('у'); break;
        case '3': sb.append('З'); break;
        case '4': sb.append('ч'); break;
        case '6': sb.append('б'); break;
        default: sb.append(c); break;
      }
    }
    return sb.toString();
  }
}
