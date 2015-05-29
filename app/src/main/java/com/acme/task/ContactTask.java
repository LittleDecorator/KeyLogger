package com.acme.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import com.acme.entity.Contact;
import com.acme.task.listener.TaskListener;

import java.util.ArrayList;
import java.util.List;

public class ContactTask extends AsyncTask<String,Void,List<Contact>> {

    TaskListener listener;
    List<Contact> contacts = new ArrayList<>();
    Context context;
    String id;

    public ContactTask(String id, Context context, TaskListener listener) {
        this.id = id;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Contact> doInBackground(String... strings) {
        //find contacts
        findContacts();
        return contacts;
    }

    //get target contacts
    private void findContacts(){
        contacts.clear();
        long stashID = -1L;
        Contact contact = null;
        List<String> mail = new ArrayList<>();
        List<String> phone = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                ContactsContract.Data.CONTACT_ID);

        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));

            if(stashID != id){
                //if got new ID
                if(contact!=null) {
                    contact.setMails(mail);
                    contact.setPhones(phone);
                    contacts.add(contact);
                }

                stashID = id;
                contact = new Contact();
                mail = new ArrayList<>();
                phone = new ArrayList<>();
                contact.setId(id);
                contact.setName(c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));

                if(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.contentEquals(c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE)))){
                    mail.add(c.getString(c.getColumnIndex(ContactsContract.Data.DATA1)));
                } else {
                    phone.add(c.getString(c.getColumnIndex(ContactsContract.Data.DATA1)));
                }

            } else {
                //if get more info for curr ID
                if(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.contentEquals(c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE)))){
                    mail.add(c.getString(c.getColumnIndex(ContactsContract.Data.DATA1)));
                } else {
                    phone.add(c.getString(c.getColumnIndex(ContactsContract.Data.DATA1)));
                }
            }
        }

        //BAD BAD BAD
        contact.setMails(mail);
        contact.setPhones(phone);
        contacts.add(contact);

        System.out.println(contacts);
    }

    @Override
    protected void onPostExecute(List<Contact> contacts) {
        listener.taskComplete(contacts);
    }
}
