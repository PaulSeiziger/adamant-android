package im.adamant.android.ui.holders;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import im.adamant.android.R;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.subjects.PublishSubject;

public class ServerNodeHolder extends RecyclerView.ViewHolder {
    private Context context;

    private TextView serverNameView;
    private TextView serverStatusView;
    private ImageView deleteButton;

    public ServerNodeHolder(
            View itemView,
            Context context,
            PublishSubject<Integer> deleteSubject,
            PublishSubject<Integer> switchSubject
    ) {
        super(itemView);

        this.context = context;

        serverNameView = itemView.findViewById(R.id.list_item_servernode_tv_name);
        serverStatusView = itemView.findViewById(R.id.list_item_servernode_tv_status);
        deleteButton = itemView.findViewById(R.id.list_item_servernode_ibtn_delete);

        deleteButton.setOnClickListener(v -> deleteSubject.onNext(getAdapterPosition()));
        itemView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    switchSubject.onNext(getAdapterPosition());
                    return super.onDoubleTap(e);
                }
            });


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public void bind(ServerNode serverNode){
        if (serverNode != null){
            serverNameView.setText(serverNode.getUrl());

            if (serverNode.getStatus() == ServerNode.Status.UNAVAILABLE) {
                serverNameView.setTextColor(ContextCompat.getColor(context, R.color.statusUnavailable));
            } else {
                serverNameView.setTextColor(ContextCompat.getColor(context, R.color.onPrimary));
            }

            serverStatusView.setTextColor(ContextCompat.getColor(context, detectStatusColor(serverNode)));

            String statusString = "";
            if (serverNode.getStatus() == ServerNode.Status.ACTIVE || serverNode.getStatus() == ServerNode.Status.CONNECTED) {
                statusString = String.format(
                        Locale.ENGLISH,
                        context.getString(R.string.activity_nodes_list_active_node_status),
                        detectStatus(serverNode),
                        serverNode.getPingInMilliseconds()
                );
            } else {
                statusString = String.format(
                        Locale.ENGLISH,
                        context.getString(R.string.activity_nodes_list_inactive_node_status),
                        detectStatus(serverNode)
                );
            }

            serverStatusView.setText(statusString);
        }
    }

    private int detectStatusColor(ServerNode serverNode) {
        switch (serverNode.getStatus()){
            case ACTIVE:
                return R.color.statusActive;
            case CONNECTED:
                return R.color.statusConnected;
            case CONNECTING:
                return R.color.statusConnecting;
            case UNAVAILABLE:
                return R.color.statusUnavailable;
            default:
                return R.color.statusUnavailable;
        }
    }

    private String detectStatus(ServerNode serverNode) {
        switch (serverNode.getStatus()){
            case ACTIVE:
                return context.getString(R.string.node_status_active);
            case CONNECTED:
                return context.getString(R.string.node_status_connected);
            case CONNECTING:
                return context.getString(R.string.node_status_connecting);
            case UNAVAILABLE:
                return context.getString(R.string.node_status_unavailable);
            default:
                return context.getString(R.string.node_status_unavailable);
        }
    }
}
