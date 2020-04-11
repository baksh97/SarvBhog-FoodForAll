//package com.example.sarvbhog.Adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.sarvbhog.R;
//import com.example.sarvbhog.Classes.RequestClass;
//
//import java.util.List;
//
//public class distributorsAdapter {
//}

package com.example.sarvbhog.Adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.recyclerview.widget.RecyclerView;

        import com.example.sarvbhog.Classes.DistributorClass;
        import com.example.sarvbhog.R;
        import com.example.sarvbhog.Classes.RequestClass;

        import java.util.List;

//public class requestsAdapter {
//}

class distributorsAdapter extends RecyclerView.Adapter<distributorsAdapter.MyViewHolder> {

    private List<DistributorClass> msgs;
    private Context context;
    private String currentUserId;
    final int MSG_TYPE_RIGHT=0,MSG_TYPE_LEFT=1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        ImageView placeimg_iv;
//        TextView placename_tv, discount_tv;
        TextView msgTv;
        public MyViewHolder(View view) {
            super(view);
//            msgTv = (TextView) view.findViewById(R.id.msg_tv_chat);
//            placeimg_iv = (ImageView) view.findViewById(R.id.placeimage_iv_placeitemlist);
//            placename_tv = (TextView) view.findViewById(R.id.placecname_tv_placeitemlist);

        }
    }

    public distributorsAdapter(List<RequestClass> msgs, Context context, String currentUserId)
    {
//        this.msgs = msgs;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    public distributorsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
//        if(viewType==MSG_TYPE_LEFT){
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_receive,parent, false);
//            return new distributorsAdapter.MyViewHolder(itemView);
//        }
//        else{
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_send,parent, false);
//            return new distributorsAdapter.MyViewHolder(itemView);
//        }
        return null;

    }

    @Override
    public void onBindViewHolder(final distributorsAdapter.MyViewHolder holder, final int position)
    {
//        holder.msgTv.setText(msgs.get(position).text);
    }

    @Override
    public int getItemCount()
    {
        return msgs.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if(msgs.get(position).senderId.equals(currentUserId)){
//            return MSG_TYPE_RIGHT;
//        }
//        else return MSG_TYPE_LEFT;
        return 0;
    }
}

