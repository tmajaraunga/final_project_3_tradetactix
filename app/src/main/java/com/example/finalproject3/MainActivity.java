// In MainActivity.java

package com.example.finalproject3;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.finalproject3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Define top-level destinations so the Up button doesn't appear on them
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.loginFragment, R.id.matchListFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Show/Hide Toolbar based on destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment) {
                binding.toolbar.setVisibility(View.GONE);
            } else {
                binding.toolbar.setVisibility(View.VISIBLE);
            }
        });

        // The default FloatingActionButton is no longer needed
        binding.fab.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
