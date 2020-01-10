package com.yxc.websocket.util;

import android.content.Context;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;


public class DialogUtils {
    public static void TwoButtonDialog(Context context, String content, OnBtnClickL onBtnClickL) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(content)
                .showAnim(new BounceTopEnter())
                .title("Warm prompt")
                .btnText("CANCEL", "CONFIRM")
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                onBtnClickL);
    }

    public static void OneButtonDialog(Context context, String content) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(content)//
                .btnNum(1)
                .title("Warm prompt")
                .btnText("CONFIRM")//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                });
    }

    public static void OneButtonDialog(Context context, String content, OnBtnClickL onBtnClickL) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(content)//
                .btnNum(1)
                .title("Warm prompt")
                .btnText("CONFIRM")//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setOnBtnClickL(onBtnClickL);
    }
}
