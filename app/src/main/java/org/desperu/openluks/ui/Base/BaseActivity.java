package org.desperu.openluks.ui.Base;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.desperu.openluks.R;
import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    // --------------
    // BASE METHODS
    // --------------

    protected abstract int getActivityLayout();
    protected abstract void configureDesign();

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        this.configureDesign();
    }

    // --------------
    // DESIGN
    // --------------

    protected void configureToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void configureUpButton(){
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
