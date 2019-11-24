package com.qb.toolbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qb.toolbox.R;
import com.qb.toolbox.adapter.AppAdapter;

import java.util.List;

public class FeaturesActivity extends AppCompatActivity {
    private static final String TAG = "FeaturesActivity";

//    private static final String NRF_CONNECT_CATEGORY = "com.qb.toolbox.LAUNCHER";
//    private static final String UTILS_CATEGORY = "com.qb.toolbox.UTILS";
    private static final String NRF_CONNECT_PACKAGE = "no.nordicsemi.android.mcp";
    private static final String NRF_CONNECT_CLASS = NRF_CONNECT_PACKAGE + ".DeviceListActivity";
    private static final String NRF_CONNECT_MARKET_URI = "market://details?id=no.nordicsemi.android.mcp";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        final Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = mDrawerLayout = findViewById(R.id.drawer_layout);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the drawer toggle as the DrawerListener
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                // Disable the Hamburger icon animation
                super.onDrawerSlide(drawerView, 0);
            }
        };
        drawer.addDrawerListener(mDrawerToggle);

        // setup plug-ins in the drawer
        setupPluginsInDrawer((ViewGroup) drawer.findViewById(R.id.plugin_container));

        // configure the app grid
        final GridView grid = findViewById(R.id.grid);
        grid.setAdapter(new AppAdapter(this));
        grid.setEmptyView(findViewById(android.R.id.empty));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_about:
                final AppHelpFragment fragment = AppHelpFragment.getInstance(R.string.about_text, true);
                fragment.show(getSupportFragmentManager(), null);
                break;
        }
        return true;
    }

    private void setupPluginsInDrawer(final ViewGroup container) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final PackageManager pm = getPackageManager();

        // look for nRF Connect
        final Intent nrfConnectIntent = new Intent(Intent.ACTION_MAIN);
        nrfConnectIntent.addCategory(getString(R.string.app_category));  //com.qb.toolbox.LAUNCHER
        nrfConnectIntent.setClassName(NRF_CONNECT_PACKAGE, NRF_CONNECT_CLASS);  //no.nordicsemi.android.mcp  //no.nordicsemi.android.mcp.DeviceListActivity
        final ResolveInfo nrfConnectInfo = pm.resolveActivity(nrfConnectIntent, 0);

        // configure link to nRF Connect
        final TextView nrfConnectItem = container.findViewById(R.id.link_mcp);
        if (nrfConnectInfo == null) {
            nrfConnectItem.setTextColor(Color.GRAY);
            final ColorMatrix grayscale = new ColorMatrix();
            grayscale.setSaturation(0.0f);
            nrfConnectItem.getCompoundDrawables()[0].mutate().setColorFilter(new ColorMatrixColorFilter(grayscale));
        }
        nrfConnectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent action = nrfConnectIntent;
                if (nrfConnectInfo == null)
                    action = new Intent(Intent.ACTION_VIEW, Uri.parse(NRF_CONNECT_MARKET_URI));
                action.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(action);
                } catch (final ActivityNotFoundException e) {
                    Toast.makeText(FeaturesActivity.this, R.string.no_application_play, Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawers();
            }
        });

        // look for other plug-ins
        final Intent utilsIntent = new Intent(Intent.ACTION_MAIN);
        utilsIntent.addCategory(getString(R.string.app_utils_category));  //com.qb.toolbox.UTILS

        final List<ResolveInfo> appList = pm.queryIntentActivities(utilsIntent, 0);
        for (final ResolveInfo info : appList) {
            final View item = inflater.inflate(R.layout.drawer_plugin, container, false);
            final ImageView icon = item.findViewById(android.R.id.icon);
            final TextView label = item.findViewById(android.R.id.text1);

            label.setText(info.loadLabel(pm));
            icon.setImageDrawable(info.loadIcon(pm));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent();
                    intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                }
            });
            container.addView(item);
        }
    }
}
