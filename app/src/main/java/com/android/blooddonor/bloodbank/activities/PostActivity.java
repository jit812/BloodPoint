package com.android.blooddonor.bloodbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.blooddonor.bloodbank.R;
import com.android.blooddonor.bloodbank.viewmodels.UserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    MyProgressDialog pd;

    EditText text1, text2;
    Spinner spinner1, spinner2, spinner3;
    Button btnpost;
    ImageView CurrentLoc;
    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;

    String selectedItem;

    String[] Rajasthan = new String[]{
            "Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara", "Bikaner", "Bundi", "Chittorgarh", "Churu", "Dausa", "Dholpur",
            "Dungarpur", "Hanumangarh", "Jaipur", "Jaisalmer", "Jalore", "Jhalawar", "Jhunjhunu", "Jodhpur", "Karauli", "Kota", "Nagaur", "Pali", "Pratapgarh", "Rajsamand",
            "Sawai Madhopur", "Sikar", "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"
    };
    String[] karnataka = new String[]{
            "Bagalkot", "Ballari (Bellary)", "Belagavi (Belgaum)", "Bengaluru (Bangalore) Rural", "Bengaluru (Bangalore) Urban", "Bidar", "Chamarajanagar", "Chikballapur",
            "Chikkamagaluru (Chikmagalur)", "Chitradurga", "Dakshina Kannada", "Davangere", "Dharwad", "Gadag", "Hassan", "Haveri", "Kalaburagi (Gulbarga)",
            "Kodagu", "Kolar", "Koppal", "Mandya", "Mysuru (Mysore)", "Raichur", "Ramanagara", "Shivamogga (Shimoga)", "Tumakuru (Tumkur)", "Udupi",
            "Uttara Kannada (Karwar)", "Vijayapura (Bijapur)", "Yadgir"
    };
    String[] andhrapradesh = new String[]{
            "Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna", "Kurnool", "Prakasam", "Srikakulam", "Sri Potti Sriramulu Nellore", "Visakhapatnam",
            "Vizianagaram", "West Godavari", "YSR District, Kadapa (Cuddapah)"
    };
    String[] arunachalpradesh = new String[]{
            "Anjaw", "Changlang", "Dibang Valley", "East Kameng", "East Siang", "Kamle", "Kra Daadi", "Kurung Kumey", "Lepa Rada", "Lohit", "Longding", "Lower Dibang Valley",
            "Lower Siang", "Lower Subansiri", "Namsai", "Pakke Kessang", "Papum Pare", "Shi Yomi", "Siang", "Tawang", "Tirap", "Upper Siang", "Upper Subansiri", "West Kameng", "West Siang"
    };
    String[] assam = new String[]{
            "Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo", "Chirang", "Darrang", "Dhemaji", "Dhubri", "Dibrugarh",
            "Dima Hasao (North Cachar Hills)", "Goalpara", "Golaghat", "Hailakandi", "Hojai", "Jorhat", "Kamrup", "Kamrup Metropolitan", "Karbi Anglong", "Karimganj", "Kokrajhar",
            "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari", "Sivasagar", "Sonitpur", "South Salamara-Mankachar", "Tinsukia", "Udalguri", "West Karbi Anglong"
    };
    String[] bihar = new String[]{
            "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur", "Buxar", "Darbhanga", "East Champaran (Motihari)", "Gaya", "Gopalganj", "Jamui", "Jehanabad", "Kaimur (Bhabua)",
            "Katihar", "Khagaria", "Kishanganj", "Lakhisarai", "Madhepura", "Madhubani", "Munger (Monghyr)", "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia (Purnea)", "Rohtas", "Saharsa",
            "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi", "Siwan", "Supaul", "Vaishali", "West Champaran"
    };
    String[] chattisgarh = new String[]{
            "Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur", "Dantewada (South Bastar)", "Dhamtari", "Durg", "Gariyaband", "Janjgir-Champa",
            "Jashpur", "Kabirdham (Kawardha)", "Kanker (North Bastar)", "Kondagaon", "Korba", "Korea (Koriya)", "Mahasamund", "Mungeli", "Narayanpur", "Raigarh",
            "Raipur", "Rajnandgaon", "Sukma", "Surajpur", "Surguja"
    };
    String[] gujrat = new String[]{
            "Ahmedabad", "Amrel", "Anand", "Aravalli", "Banaskantha (Palanpur)", "Bharuch", "Bhavnagar", "Botad", "Chhota Udepur", "Dahod", "Dangs (Ahwa)",
            "Devbhoomi Dwarka", "Gandhinagar", "Gir", "Somnath", "Jamnagar", "Junagadh", "Kachchh", "Kheda (Nadiad)", "Mahisagar", "Mehsana",
            "Morbi", "Narmada (Rajpipla)", "Navsari", "Panchmahal (Godhra)", "Patan", "Porbandar", "Rajkot", "Sabarkantha (Himmatnagar)",
            "Surat", "Surendranagar", "Tapi (Vyara)", "Vadodara", "Valsad"
    };
    String[] haryana = new String[]{
            "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar", "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra",
            "Mahendragarh", "Nuh", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"
    };
    String[] himachal_pradesh = new String[]{
            "Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", "Lahaul & Spiti", "Mandi", "Shimla", "Sirmaur (Sirmour)", "Solan", "Una"
    };
    String[] jharkhand = new String[]{
            "Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum", "Garhwa", "Giridih", "Godda", "Gumla", "Hazaribag", "Jamtara",
            "Khunti", "Koderma", "Latehar", "Lohardaga", "Pakur", "Palamu", "Ramgarh", "Ranchi", "Sahibganj", "Seraikela-Kharsawan", "Simdega",
            "West Singhbhum"
    };
    String[] kerala = new String[]{
            "Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam", "Kottayam", "Kozhikode", "Malappuram", "Palakkad",
            "Pathanamthitta", "Thiruvananthapuram", "Thrissur", "Wayanad"
    };
    String[] madhya_pradesh = new String[]{
            "Agar Malwa", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani", "Betul", "Bhind", "Bhopal", "Burhanpur", "Chhatarpur",
            "Chhindwara", "Damoh", "Datia", "Dewas", "Dhar", "Dindori", "Guna", "Gwalior", "Harda", "Hoshangabad", "Indore", "Jabalpur",
            "Jhabua", "Katni", "Khandwa", "Khargone", "Mandla", "Mandsaur", "Morena", "Narsinghpur", "Neemuch", "Panna", "Raisen", "Rajgarh",
            "Ratlam", "Rewa", "Sagar", "Satna", "Sehore", "Seoni", "Shahdol", "Shajapur", "Sheopur", "Shivpuri", "Sidhi", "Singrauli",
            "Tikamgarh", "Ujjain", "Umaria", "Vidisha"
    };
    String[] meghalaya = new String[]{
            "East Garo Hills", "East Jaintia Hills", "East Khasi Hills", "North Garo Hills", "Ri Bhoi", "South Garo Hills", "South West Garo Hills",
            "South West Khasi Hills", "West Garo Hills", "West Jaintia Hills", "West Khasi Hill"
    };
    String[] maharashtra = new String[]{
            "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed", "Bhandara", "Buldhana", "Chandrapur", "Dhule", "Gadchiroli", "Gondia",
            "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur", "Mumbai City", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik",
            "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri", "Sangli", "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha",
            "Washim", "Yavatmal"
    };
    String[] manipur = new String[]{
            "Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West", "Jiribam", "Kakching", "Kamjong", "Kangpokpi", "Noney",
            "Pherzawl", "Senapati", "Tamenglong", "Tengnoupal", "Thoubal", "Ukhrul"
    };
    String[] mizoram = new String[]{
            "Aizawl", "Champhai", "Kolasib", "Lawngtlai", "Lunglei", "Mamit", "Saiha", "Serchhip"
    };
    String[] goa = new String[]{
            "North Goa", "South Goa"
    };
    String[] west_bengal = new String[]{
            "Alipurduar", "Bankura", "Birbhum", "Cooch Behar", "Dakshin Dinajpur (South Dinajpur)", "Darjeeling", "Hooghly", "Howrah",
            "Jalpaiguri", "Jhargram", "Kalimpong", "Kolkata", "Malda", "Murshidabad", "Nadia", "North 24 Parganas", "Paschim Medinipur (West Medinipur)",
            "Paschim (West) Burdwan (Bardhaman)", "Purba Burdwan (Bardhaman)", "Purba Medinipur (East Medinipur)", "Purulia",
            "South 24 Parganas", "Uttar Dinajpur (North Dinajpur)"
    };
    String[] uttarakhand = new String[]{
            "Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar", "Nainital", "Pauri Garhwal", "Pithoragarh", "Rudraprayag",
            "Tehri Garhwal", "Udham Singh Nagar", "Uttarkashi"
    };
    String[] uttar_pradesh = new String[]{
            "Agra", "Aligarh", "Allahabad", "Ambedkar Nagar", "Amethi (Chatrapati Sahuji Mahraj Nagar)", "Amroha (J.P. Nagar)",
            "Auraiya", "Azamgarh", "Baghpat", "Bahraich", "Ballia", "Balrampur", "Banda", "Barabanki", "Bareilly", "Basti",
            "Bhadohi", "Bijnor", "Budaun", "Bulandshahr", "Chandauli", "Chitrakoot", "Deoria", "Etah", "Etawah", "Faizabad", "Farrukhabad",
            "Fatehpur", "Firozabad", "Gautam Buddha Nagar", "Ghaziabad", "Ghazipur", "Gonda", "Gorakhpur", "Hamirpur", "Hapur (Panchsheel Nagar)",
            "Hardoi", "Hathras", "Jalaun", "Jaunpur", "Jhansi", "Kannauj", "Kanpur Dehat", "Kanpur Nagar", "Kanshiram Nagar (Kasganj)",
            "Kaushambi", "Kushinagar (Padrauna)", "Lakhimpur - Kheri", "Lalitpur", "Lucknow", "Maharajganj", "Mahoba", "Mainpuri",
            "Mathura", "Mau", "Meerut", "Mirzapur", "Moradabad", "Muzaffarnagar", "Pilibhit", "Pratapgarh", "RaeBareli", "Rampur", "Saharanpur",
            "Sambhal (Bhim Nagar)", "Sant Kabir Nagar", "Shahjahanpur", "Shamali (Prabuddh Nagar)", "Shravasti", "Siddharth Nagar", "Sitapur",
            "Sonbhadra", "Sultanpur", "Unnao", "Varanasi"
    };
    String[] tripura = new String[]{
            "Dhalai", "Gomati", "Khowai", "North Tripura", "Sepahijala", "South Tripura", "Unakoti", "West Tripura"
    };
    String[] telengana = new String[]{
            "Adilabad", "Bhadradri Kothagudem", "Hyderabad", "Jagtial", "Jangaon", "Jayashankar Bhoopalpally", "Jogulamba Gadwal", "Kamareddy",
            "Karimnagar", "Khammam", "Komaram Bheem Asifabad", "Mahabubabad", "Mahabubnagar", "Mancherial", "Medak", "Medchal", "Nagarkurnool",
            "Nalgonda", "Nirmal", "Nizamabad", "Peddapalli", "Rajanna Sircilla", "Rangareddy", "Sangareddy", "Siddipet", "Suryapet", "Vikarabad",
            "Wanaparthy", "Warangal (Rural)", "Warangal (Urban)", "Yadadri Bhuvanagiri"
    };
    String[] tamil_nadu = new String[]{
            "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kallakurichi",
            "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri", "Madurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur",
            "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivaganga", "Tenkasi", "Thanjavur", "Theni", "Thoothukudi (Tuticorin)",
            "Tiruchirappalli", "Tirunelveli", "Tirupathur", "Tiruppur", "Tiruvallur", "Tiruvannamalai", "Tiruvarur", "Vellore",
            "Viluppuram", "Virudhunagar"
    };
    String[] sikkim = new String[]{
            "East Sikkim", "North Sikkim", "South Sikkim", "West Sikkim"
    };
    String[] punjab = new String[]{
            "Amritsar", "Barnala", "Bathinda", "Faridkot", "Fatehgarh Sahib", "Fazilka", "Ferozepur", "Gurdaspur", "Hoshiarpur", "Jalandhar",
            "Kapurthala", "Ludhiana", "Mansa", "Moga", "Muktsar", "Nawanshahr (Shahid Bhagat Singh Nagar)", "Pathankot", "Patiala", "Rupnagar",
            "Sahibzada Ajit Singh Nagar (Mohali)", "Sangrur", "Tarn Taran"
    };
    String[] odisha = new String[]{
            "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack", "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghapur",
            "Jajpur", "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar (Keonjhar)", "Khordha", "Koraput", "Malkangiri", "Mayurbhanj",
            "Nabarangpur", "Nayagarh", "Nuapada", "Puri", "Rayagada", "Sambalpur", "Sonepur", "Sundargarh"
    };
    String[] nagaland = new String[]{
            "Dimapur", "Kiphire", "Kohima", "Longleng", "Mokokchung", "Mon", "Noklak", "Peren", "Phek", "Tuensang", "Wokha", "Zunheboto"
    };
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pd = new MyProgressDialog(PostActivity.this);
        pd.setMessage("Loading. Please wait...");
        pd.show();
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();


        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);
        CurrentLoc = findViewById(R.id.curloc);
        spinner1 = findViewById(R.id.SpinnerBlood);
        spinner2 = findViewById(R.id.SpinnerDivision);
        spinner3 = findViewById(R.id.inputLocality);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinner2.getSelectedItem().toString();
                if (selectedItem.equals("Rajasthan")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, Rajasthan);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Karnataka")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, karnataka);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Andhra Pradesh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, andhrapradesh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Arunachal Pradesh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, arunachalpradesh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Assam")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, assam);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Bihar")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, bihar);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Chattisgarh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, chattisgarh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Goa")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, goa);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Gujrat")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, gujrat);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Haryana")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, haryana);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Himachal Pradesh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, himachal_pradesh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Jharkhand")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, jharkhand);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Kerala")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, kerala);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Madhya Pradesh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, madhya_pradesh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Maharashtra")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, maharashtra);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Manipur")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, manipur);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Meghalaya")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, meghalaya);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Mizoram")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, mizoram);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Nagaland")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, nagaland);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Odisha")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, odisha);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Punjab")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, punjab);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Sikkim")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, sikkim);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Tamil Nadu")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, tamil_nadu);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Telengana")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, telengana);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Tripura")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, tripura);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Uttar Pradesh")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, uttar_pradesh);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("Uttarakhand")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, uttarakhand);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
                if (selectedItem.equals("West Bengal")) {
                    Spinner s = (Spinner) findViewById(R.id.inputLocality);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, west_bengal);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnpost = findViewById(R.id.postbtn);

        cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        month += 1;
        Time = "";
        Date = "";
        String ampm = "AM";

        if (cal.get(Calendar.AM_PM) == 1) {
            ampm = "PM";
        }

        if (hour < 10) {
            Time += "0";
        }
        Time += hour;
        Time += ":";

        if (min < 10) {
            Time += "0";
        }

        Time += min;
        Time += (" " + ampm);

        Date = day + "/" + month + "/" + year;

        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

        if (cur_user == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        try {
            btnpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    final Query findname = fdb.getReference("users").child(uid);

                    if (text1.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Enter your contact number!",
                                Toast.LENGTH_LONG).show();
                    } else if (text2.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Enter your location!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        findname.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    db_ref.child(uid).child("Name").setValue(dataSnapshot.getValue(UserData.class).getName());
                                    db_ref.child(uid).child("Contact").setValue(text1.getText().toString());
                                    db_ref.child(uid).child("Address").setValue(text2.getText().toString());
                                    db_ref.child(uid).child("Division").setValue(spinner2.getSelectedItem().toString());
                                    db_ref.child(uid).child("Locality").setValue(spinner3.getSelectedItem().toString());
                                    db_ref.child(uid).child("BloodGroup").setValue(spinner1.getSelectedItem().toString());
                                    db_ref.child(uid).child("Time").setValue(Time);
                                    db_ref.child(uid).child("Date").setValue(Date);
                                    Toast.makeText(PostActivity.this, "Your post has been created successfully",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PostActivity.this, Dashboard.class));

                                } else {
                                    Toast.makeText(getApplicationContext(), "Database error occured.",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("User", databaseError.getMessage());

                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        pd.dismiss();
        CurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                // method to get the location
                getLastLocation();
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation () {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            text2.setText(location.getLatitude() + "" + location.getLongitude() + "");
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);
                            text2.setText(cityName + stateName + countryName);

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData () {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            text2.setText("Latitude: " + mLastLocation.getLatitude() + "" + "Longitude: " + mLastLocation.getLongitude() + "");
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getSubLocality();
            String stateName = addresses.get(0).getAdminArea();
            text2.setText(cityName + "\n" + stateName);
        }
    };

    // method to check for permissions
    private boolean checkPermissions () {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions () {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled () {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
                                @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }

        }
    }
    public void backbb (View view){

        onBackPressed();
    }
}
