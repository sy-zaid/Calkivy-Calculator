package org.kivy.android.launcher;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.renpy.android.ResourceManager;

public class ProjectAdapter extends ArrayAdapter<Project> {
    private ResourceManager resourceManager;

    public ProjectAdapter(Activity context) {
        super(context, 0);
        this.resourceManager = new ResourceManager(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Project p = (Project) getItem(position);
        View v = this.resourceManager.inflateView("chooser_item");
        ((TextView) this.resourceManager.getViewById(v, "title")).setText(p.title);
        ((TextView) this.resourceManager.getViewById(v, "author")).setText(p.author);
        ((ImageView) this.resourceManager.getViewById(v, "icon")).setImageBitmap(p.icon);
        return v;
    }
}
