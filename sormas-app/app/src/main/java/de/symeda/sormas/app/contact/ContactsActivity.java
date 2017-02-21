package de.symeda.sormas.app.contact;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.AbstractRootTabActivity;

public class ContactsActivity extends AbstractRootTabActivity {

    private ContactsListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.contacts_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_contacts));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ContactsListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);

        SyncContactsTask.syncContacts(getSupportFragmentManager());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reload:
                SyncContactsTask.syncContacts(getSupportFragmentManager());
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
