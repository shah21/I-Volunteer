package com.curiocodes.i_volunteer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curiocodes.i_volunteer.AddOn.General;
import com.curiocodes.i_volunteer.Models.Models;
import com.curiocodes.i_volunteer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Models.Posts> list;
    private Context context;
    private OnItemClick onItemClick;
    private String type;


    public interface OnItemClick{
        void  onClick(int pos);
    }

    public PostsAdapter(String type,List<Models.Posts> list, Context context,OnItemClick onItemClick) {
        this.type = type;
        this.list = list;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (!type.equals("request")) {
            view = LayoutInflater.from(context).inflate(R.layout.single_post, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.post_request, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.username.setText(list.get(position).getName());
        holder.title.setText(list.get(position).getTitle());
        holder.content.setText(list.get(position).getContent());
        holder.place.setText(list.get(position).getPlace());
        final String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(list.get(position).getDate());
        holder.date.setText(date);
        Picasso.get().load(list.get(position).getUri()).placeholder(R.drawable.profile_placeholder).fit().into(holder.circleImageView);
        if (list.get(position).isTrue()){
            holder.assuredIcon.setImageResource(R.drawable.badge);
        }
        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(position);
            }
        });

        if (type.equals("request")){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Posts");

            Query query = db.child(list.get(position).getKey()).child("Requests")
                    .orderByChild("requestId").equalTo(auth.getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Models.Request request = dataSnapshot.getValue(Models.Request.class);
                        if (request.isAccept()){
                            General.toast(context,"ok");
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username,title,content,date,place;
        private ImageView assuredIcon;
        private CircleImageView circleImageView;
        private LinearLayout body;
        private LinearLayout acceptBlock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.body);
            place = itemView.findViewById(R.id.place);
            username = itemView.findViewById(R.id.name);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            assuredIcon = itemView.findViewById(R.id.assureIcon);
            date = itemView.findViewById(R.id.date);
            circleImageView = itemView.findViewById(R.id.profile_image);

            if (type.equals("request")){
                acceptBlock = itemView.findViewById(R.id.acceptBlock);
            }

        }
    }
}
