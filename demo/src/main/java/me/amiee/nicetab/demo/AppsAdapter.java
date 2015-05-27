package me.amiee.nicetab.demo;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


public class AppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickedListener mOnItemClickedListener;

    public interface OnItemClickedListener {
        void onItemClicked(AppsAdapter appsAdapter, int position);
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView iconIv;
        public TextView nameTv;

        public AppViewHolder(View itemView) {
            super(itemView);
            iconIv = (SimpleDraweeView) itemView.findViewById(R.id.list_item_app_icon_iv);
            nameTv = (TextView) itemView.findViewById(R.id.list_item_app_name_tv);
        }
    }

    public AppsAdapter(OnItemClickedListener onItemClickedListener) {
        mOnItemClickedListener = onItemClickedListener;
        mApps = new ArrayList<>();
        mApps.add(new App("StarMaker",
                "http://iosicongallery.com/iosicongallery/img/256/starmaker-2015.png",
                "https://itunes.apple.com/us/app/starmaker-sing-+-video-+-auto/id342138881"));
        mApps.add(new App("Let's Go Rocket - Ultimate Endless Space Adventure",
                "http://iosicongallery.com/iosicongallery/img/512/lets-go-rocket-2015.png",
                "https://itunes.apple.com/us/app/lets-go-rocket-ultimate-endless/id974628899"));
        mApps.add(new App("Vinyl Music and Video Files Manager",
                "http://iosicongallery.com/iosicongallery/img/256/vinyl-music-video-files-manager-2015.png",
                "https://itunes.apple.com/us/app/vinyl-music-video-files-manager/id938821819"));
        mApps.add(new App("Meh.",
                "http://iosicongallery.com/iosicongallery/img/256/meh.-2015.png",
                "https://itunes.apple.com/us/app/meh./id987393491"));
        mApps.add(new App("Enlight",
                "http://iosicongallery.com/iosicongallery/img/256/enlight-2015.png",
                "https://itunes.apple.com/us/app/enlight/id930026670"));
        mApps.add(new App("Sellf",
                "http://iosicongallery.com/iosicongallery/img/256/sellf-2015.png",
                "https://itunes.apple.com/us/app/sellf-your-personal-crm/id685969957"));
        mApps.add(new App("Alto's Adventure",
                "http://iosicongallery.com/iosicongallery/img/256/altos-adventure-2015.png",
                "https://itunes.apple.com/us/app/altos-adventure/id950812012"));
        mApps.add(new App("Monument Valley",
                "http://iosicongallery.com/iosicongallery/img/256/monument-valley-2015.png",
                "https://itunes.apple.com/us/app/monument-valley/id728293409"));
        mApps.add(new App("Tubex for YouTube",
                "http://iosicongallery.com/iosicongallery/img/256/tubex-for-youtube-2014.png",
                "https://itunes.apple.com/us/app/tubex-for-youtube/id939906112"));
        mApps.add(new App("AirPano Travel Book",
                "http://iosicongallery.com/iosicongallery/img/256/airpano-travel-book-2014.png",
                "https://itunes.apple.com/us/app/airpano-travel-book/id887138564"));
        mApps.add(new App("StarMaker",
                "http://iosicongallery.com/iosicongallery/img/256/starmaker-2015.png",
                "https://itunes.apple.com/us/app/starmaker-sing-+-video-+-auto/id342138881"));
        mApps.add(new App("Let's Go Rocket - Ultimate Endless Space Adventure",
                "http://iosicongallery.com/iosicongallery/img/512/lets-go-rocket-2015.png",
                "https://itunes.apple.com/us/app/lets-go-rocket-ultimate-endless/id974628899"));
        mApps.add(new App("Vinyl Music and Video Files Manager",
                "http://iosicongallery.com/iosicongallery/img/256/vinyl-music-video-files-manager-2015.png",
                "https://itunes.apple.com/us/app/vinyl-music-video-files-manager/id938821819"));
        mApps.add(new App("Meh.",
                "http://iosicongallery.com/iosicongallery/img/256/meh.-2015.png",
                "https://itunes.apple.com/us/app/meh./id987393491"));
        mApps.add(new App("Enlight",
                "http://iosicongallery.com/iosicongallery/img/256/enlight-2015.png",
                "https://itunes.apple.com/us/app/enlight/id930026670"));
        mApps.add(new App("Sellf",
                "http://iosicongallery.com/iosicongallery/img/256/sellf-2015.png",
                "https://itunes.apple.com/us/app/sellf-your-personal-crm/id685969957"));
        mApps.add(new App("Alto's Adventure",
                "http://iosicongallery.com/iosicongallery/img/256/altos-adventure-2015.png",
                "https://itunes.apple.com/us/app/altos-adventure/id950812012"));
        mApps.add(new App("Monument Valley",
                "http://iosicongallery.com/iosicongallery/img/256/monument-valley-2015.png",
                "https://itunes.apple.com/us/app/monument-valley/id728293409"));
        mApps.add(new App("Tubex for YouTube",
                "http://iosicongallery.com/iosicongallery/img/256/tubex-for-youtube-2014.png",
                "https://itunes.apple.com/us/app/tubex-for-youtube/id939906112"));
        mApps.add(new App("AirPano Travel Book",
                "http://iosicongallery.com/iosicongallery/img/256/airpano-travel-book-2014.png",
                "https://itunes.apple.com/us/app/airpano-travel-book/id887138564"));
    }

    private List<App> mApps;

    public App getItem(int position) {
        return mApps.get(position);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AppViewHolder itemViewHolder = (AppViewHolder) holder;
        Uri uri = Uri.parse(mApps.get(position).getIconUrl());
        itemViewHolder.iconIv.setImageURI(uri);
        itemViewHolder.nameTv.setText(mApps.get(position).getName());
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClicked(AppsAdapter.this, position);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_app, parent, false);
        return new AppViewHolder(v);
    }
}
