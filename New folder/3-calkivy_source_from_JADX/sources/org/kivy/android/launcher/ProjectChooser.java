package org.kivy.android.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.Arrays;
import org.renpy.android.ResourceManager;

public class ProjectChooser extends Activity implements AdapterView.OnItemClickListener {
    ResourceManager resourceManager;
    String urlScheme;

    public void onStart() {
        super.onStart();
        ResourceManager resourceManager2 = new ResourceManager(this);
        this.resourceManager = resourceManager2;
        this.urlScheme = resourceManager2.getString("urlScheme");
        setTitle(this.resourceManager.getString("appName"));
        File dir = new File(Environment.getExternalStorageDirectory(), this.urlScheme);
        File[] entries = dir.listFiles();
        if (entries == null) {
            entries = new File[0];
        }
        Arrays.sort(entries);
        ProjectAdapter projectAdapter = new ProjectAdapter(this);
        for (File d : entries) {
            Project p = Project.scanDirectory(d);
            if (p != null) {
                projectAdapter.add(p);
            }
        }
        if (projectAdapter.getCount() != 0) {
            View v = this.resourceManager.inflateView("project_chooser");
            ListView l = (ListView) this.resourceManager.getViewById(v, "projectList");
            l.setAdapter(projectAdapter);
            l.setOnItemClickListener(this);
            setContentView(v);
            return;
        }
        View v2 = this.resourceManager.inflateView("project_empty");
        ((TextView) this.resourceManager.getViewById(v2, "emptyText")).setText("No projects are available to launch. Please place a project into " + dir + " and restart this application. Press the back button to exit.");
        setContentView(v2);
    }

    public void onItemClick(AdapterView parent, View view, int position, long id) {
        Intent intent = new Intent("org.kivy.LAUNCH", Uri.fromParts(this.urlScheme, ((Project) parent.getItemAtPosition(position)).dir, ""));
        intent.setClassName(getPackageName(), "org.kivy.android.PythonActivity");
        startActivity(intent);
        finish();
    }
}
