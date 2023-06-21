package com.batsworks.simplewebview.notification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import com.batsworks.simplewebview.R;

import java.util.function.Function;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BatsAlertDialog {

    public static void alert(Context context, String title, String message, String functionValue, Function<String, Void> function) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        dialog.show();
        dialog.getWindow().setLayout(WRAP_CONTENT, WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnimation;
        dialog.getWindow().setGravity(CENTER);

        AppCompatButton buttonExecute = dialog.findViewById(R.id.button_execute);
        TextView messageView = dialog.findViewById(R.id.message_dialog);
        TextView titleView = dialog.findViewById(R.id.title_dialog);

        titleView.setText(title);
        messageView.setText(message);
        buttonExecute.setText("confirmar!");
        buttonExecute.setOnClickListener(click -> function.apply(functionValue));
    }

    public static void alert(Context context, String functionValue, Function<String, Void> function) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null);
        builder.setMessage(null);
        builder.setCancelable(true);
        builder.setPositiveButton("ok", (dialog, which) -> {
            function.apply(functionValue);
            dialog.dismiss();
        });
        builder.create().show();
    }

}
