package com.yxc.websocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yxc.websocket.R;
import com.yxc.websocket.modle.ChatMessage;
import com.yxc.websocket.util.DateUtils;

import java.util.List;

public class Adapter_ChatMessage extends BaseAdapter {
    List<ChatMessage> mChatMessageList;
    LayoutInflater inflater;
    Context context;

    public Adapter_ChatMessage(Context context, List<ChatMessage> list) {
        this.mChatMessageList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessageList.get(position).getIsMeSend() == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public int getCount() {
        return mChatMessageList.size();
    }


    @Override
    public Object getItem(int i) {
        return mChatMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatMessage mChatMessage = mChatMessageList.get(i);
        String content = mChatMessage.getMsg();
        int isMeSend = mChatMessage.getIsMeSend();
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if (isMeSend == 0) {
                view = inflater.inflate(R.layout.item_chat_receive_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
                holder.tv_display_name = view.findViewById(R.id.tv_display_name);
            } else {
                view = inflater.inflate(R.layout.item_chat_send_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
                holder.tv_isRead = view.findViewById(R.id.tv_isRead);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.tv_sendtime.setText(DateUtils.getYMDHMS(mChatMessage.getCreate()));
        holder.tv_content.setVisibility(View.VISIBLE);
        holder.tv_content.setText(content);


        if (isMeSend == 1) {
            holder.tv_isRead.setText("");
        } else {
            holder.tv_display_name.setVisibility(View.VISIBLE);
            holder.tv_display_name.setText(mChatMessage.getSendName());
        }

        return view;
    }

    class ViewHolder {
        private TextView tv_content, tv_sendtime, tv_display_name, tv_isRead;
    }
}
