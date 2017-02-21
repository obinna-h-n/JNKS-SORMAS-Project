package de.symeda.sormas.app.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.AbstractRootTabActivity;

public class EventsActivity extends AbstractRootTabActivity {

    private EventsListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cases_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_events));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new EventsListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);

        SyncEventsTask.syncEvents(getSupportFragmentManager());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cases_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                SyncEventsTask.syncEvents(getSupportFragmentManager());
                return true;

            case R.id.action_new_case:
                showNewEventView();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNewEventView() {
        Intent intent = new Intent(this, EventEditActivity.class);
        intent.putExtra(EventEditActivity.NEW_EVENT, true);
        startActivity(intent);
    }
}
