package com.yxc.websocket.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yxc.websocket.R;
import com.yxc.websocket.modle.MessageListBean;

/**
 * Created by cocoon on 2019/11/8 0008.
 */

public class MessageListAdapter extends BaseQuickAdapter<MessageListBean.DataBean, BaseViewHolder> {
    public MessageListAdapter(Context context) {
        super(R.layout.message_item_layout, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageListBean.DataBean item) {
        helper.setText(R.id.tv_message_item_name, item.getUserName());
        helper.setGone(R.id.tv_message_item_new, item.isNew());
    }
}
