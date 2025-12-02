package com.example.snapsale.network.requests;

import com.example.snapsale.R;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


// HELPFUL LINKS:
// 1). "How to use the Google Translate API in an Android app using Java", Coffee Programmer, URL: https://www.youtube.com/watch?v=cwLZGGTg-zg&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=53&ab_channel=CoffeeProgrammer
// 2). "Android HTML: Html.fromHtml() deprecated in API 24", Bugs Flow, URL: https://www.youtube.com/watch?v=hW1CMVJEmzk&ab_channel=BugsFlow


public class TranslateRequest {
    private static final String TAG = TranslateRequest.class.getName();

    public interface TranslateCallback {
        void onTranslationComplete(String translatedText);
    }

    public static void translate(Context context, String targetLanguage, String sourceLanguage, final String string, final TranslateCallback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Translate translate = TranslateOptions.newBuilder().setApiKey(context.getResources().getString(R.string.google_translate_key)).build().getService();
                    Translation translation = translate.translate(string, Translate.TranslateOption.targetLanguage(targetLanguage), Translate.TranslateOption.sourceLanguage(sourceLanguage));

                    return translation.getTranslatedText();
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String translatedText) {
                if (translatedText != null) {
                    String decodedText = Html.fromHtml(translatedText, Html.FROM_HTML_MODE_LEGACY).toString();
                    callback.onTranslationComplete(decodedText);
                }
                else {
                    Log.e(TAG, "Couldn't translate the string provided.");
                }
            }
        }.execute();
    }
}
