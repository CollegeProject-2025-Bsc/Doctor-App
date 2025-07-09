package com.example.doctorapp.VIEW

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.doctorapp.MODEL.DepartmentModel
import com.example.doctorapp.MODEL.DoctorModel
import com.example.doctorapp.MODEL.UserModel
import com.example.doctorapp.R
import com.example.doctorapp.VIEW_MODEL.DocViewModel
import com.example.doctorapp.adapter.DepartmentAdapter
import com.example.doctorapp.adapter.DocViewCardAdapter
import com.example.doctorapp.adapter.KeywordAdapter
import com.example.doctorapp.databinding.ActivitySearchBinding
import com.example.doctorapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Watchable

class SearchActivity : AppCompatActivity() {
    lateinit var searchBinding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        var searchJob: Job? = null
        var search:List<DoctorModel> = emptyList()
        val viewmodel = ViewModelProvider(this)[DocViewModel::class.java]

        val keywords = listOf(
            "kidney problem",
            "kidney failure",
            "chronic kidney disease",
            "acute kidney injury",
            "kidney stones",
            "nephrotic syndrome",
            "polycystic kidney disease",
            "glomerulonephritis",
            "urinary tract infection",
            "uti",
            "blood in urine",
            "frequent urination",
            "painful urination",
            "swelling in legs or feet",
            "foamy urine",
            "high creatinine",
            "renal failure",
            "renal disease",
            "nephrology",
            "dialysis",
            "kidney pain",
            "kidney transplant",
            "kidney",

            "andrology",
            "male health",
            "testosterone therapy",
            "erectile dysfunction",
            "male fertility",
            "infertility treatment",
            "sperm count",
            "penis health",
            "prostate problems",
            "male hormone treatment",
            "sexual health",
            "low libido",
            "prostate cancer",
            "premature ejaculation",
            "vasectomy",
            "male reproductive health",
            "testicular cancer",
            "andropause",

            "pediatrics",
            "children's health",
            "pediatrician",
            "child growth",
            "child development",
            "immunization",
            "vaccination",
            "newborn care",
            "baby doctor",
            "pediatric exam",
            "pediatric surgery",
            "adhd",
            "asthma",
            "childhood diseases",
            "autism",
            "growth hormone treatment",
            "pediatric cardiology",
            "pediatric orthopedics",
            "pediatric neurology",
            "pediatric endocrinology",
            "child obesity",
            "pediatric allergies",
            "neonatal care",
            "pediatric gastroenterology",

            "dermatology",
            "skin care",
            "acne treatment",
            "eczema",
            "psoriasis",
            "rashes",
            "skin cancer",
            "dermatologist",
            "skin rash",
            "sunburn",
            "moles",
            "skin pigmentation",
            "botox",
            "laser hair removal",
            "anti-aging treatments",
            "wrinkles",
            "stretch marks",
            "hair loss treatment",
            "rosacea",
            "melasma",
            "tattoo removal",
            "skin infections",
            "skin allergy",
            "skin biopsy",
            "nail fungus",
            "laser treatment for scars",

            "cardiology",
            "heart disease",
            "heart problems",
            "hypertension",
            "chest pain",
            "ecg",
            "arrhythmia",
            "heart attack",
            "heart failure",
            "cardiologist",
            "blood pressure",
            "cholesterol",
            "coronary artery disease",
            "stroke prevention",
            "pacemaker",
            "cardiac surgery",
            "valve problems",
            "heart check-up",
            "angina",
            "heart murmur",
            "cardiomyopathy",
            "arrhythmic heart disease",
            "peripheral artery disease",
            "high triglycerides",
            "cardiac rehabilitation",
            "hypertrophic cardiomyopathy",

            "neurology",
            "brain health",
            "headache",
            "migraine",
            "seizures",
            "neurologist",
            "stroke",
            "alzheimer's disease",
            "parkinson's disease",
            "memory problems",
            "numbness",
            "nervous system",
            "tremors",
            "dizziness",
            "spinal cord injury",
            "multiple sclerosis",
            "sleep disorders",
            "epilepsy",
            "neuropathy",
            "nerve pain",
            "dementia",
            "nervous system disorders",
            "neurodegenerative disease",
            "brain tumor",

            "hepatology",
            "liver health",
            "hepatitis",
            "fatty liver",
            "cirrhosis",
            "liver transplant",
            "liver disease",
            "jaundice",
            "liver enzymes",
            "hepatitis b",
            "hepatitis c",
            "gallbladder problems",
            "liver cancer",
            "fatty liver treatment",
            "liver function test",
            "liver biopsy",
            "hepatologist",
            "liver cirrhosis",
            "cholestasis",
            "hepatitis d",
            "hepatocellular carcinoma",

            "oncology",
            "cancer",
            "tumor",
            "oncologist",
            "chemotherapy",
            "radiotherapy",
            "radiation therapy",
            "breast cancer",
            "lung cancer",
            "colon cancer",
            "leukemia",
            "lymphoma",
            "cancer treatment",
            "immunotherapy",
            "cancer screening",
            "cancer surgery",
            "metastasis",
            "tumor markers",
            "palliative care",
            "radiation oncology",
            "melanoma",
            "breast cancer treatment",
            "colorectal cancer",
            "cancer immunotherapy",

            "gynecology",
            "women's health",
            "ob",
            "gyn",
            "menstrual cycle",
            "pregnancy",
            "fertility",
            "contraception",
            "birth control",
            "menopause",
            "fibroids",
            "polycystic ovary syndrome",
            "pcos",
            "vaginal infections",
            "cervical screening",
            "ovarian cancer",
            "breast exam",
            "pelvic pain",
            "hysterectomy",
            "pregnancy checkup",
            "postpartum care",
            "endometriosis",
            "hpv vaccination",
            "pregnancy complications",
            "vaginal prolapse",

            "ent",
            "ear problems",
            "nose problems",
            "throat problems",
            "hearing loss",
            "sinusitis",
            "allergy",
            "tonsilitis",
            "ear infection",
            "sleep apnea",
            "snoring",
            "vertigo",
            "sore throat",
            "nasal congestion",
            "deafness",
            "sinus infection",
            "ear wax removal",
            "nasal surgery",
            "throat cancer",
            "voice disorder",

            "psychology",
            "mental health",
            "therapy",
            "stress",
            "anxiety",
            "depression",
            "psychologist",
            "counseling",
            "family therapy",
            "addiction",
            "anger management",
            "cognitive therapy",
            "psychotherapy",
            "mental illness",
            "bipolar disorder",
            "ocd",
            "grief counseling",
            "relationship therapy",
            "ptsd",
            "child therapy",
            "couples therapy",
            "addiction therapy",

            "general medicine",
            "primary care",
            "general practitioner",
            "check-up",
            "health screening",
            "fever",
            "cold",
            "stomach pain",
            "fatigue",
            "cough",
            "diabetes",
            "annual check-up",
            "chronic illness",
            "physical exam",
            "preventative care",
            "health consultation",

            "ophthalmology",
            "eye problems",
            "vision",
            "glaucoma",
            "cataract",
            "eye exam",
            "lasik",
            "astigmatism",
            "macular degeneration",
            "dry eyes",
            "vision correction",
            "contact lenses",
            "retina",
            "eye surgery",
            "eye infection",
            "color blindness",
            "night blindness",
            "eye floaters",
            "diabetic retinopathy"
        )

        searchBinding.sRecycler.layoutManager = LinearLayoutManager(this)

        searchBinding.rKey.layoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        searchBinding.rKey.adapter = KeywordAdapter(keywords){
            lifecycleScope.launch {
                val searchResult = viewmodel.getSearchResult(it)
                if (searchResult.isSuccessful){
                    search = searchResult.body()!!

                    searchBinding.searchBar.setText(it)
                    searchBinding.helper.visibility = View.GONE
                    searchBinding.searchResult.visibility = View.VISIBLE
                    searchBinding.total.text = "Total doctor found ${search.size}"
                    val adapter = DocViewCardAdapter(search){

                    }
                    searchBinding.sRecycler.adapter = adapter

                    Log.d("@search", search.toString())
                }
            }
        }

        searchBinding.searchBar.doOnTextChanged {text, _, _, _ ->
            Log.d("@search", text.toString())
                searchJob?.cancel()
                if (text!!.isNotEmpty()){
                    searchJob = lifecycleScope.launch {

                        delay(1000)
                        val searchResult = viewmodel.getSearchResult(text.toString())
                        if (searchResult.isSuccessful){
                            search = searchResult.body()!!

                            searchBinding.helper.visibility = View.GONE
                            searchBinding.searchResult.visibility = View.VISIBLE

                            searchBinding.total.text = "Total doctor found ${search.size}"

                            val adapter = DocViewCardAdapter(search){

                            }
                            searchBinding.sRecycler.adapter = adapter

                            Log.d("@search", search.size.toString())
                        }
                    }
                }else{
                    searchBinding.helper.visibility = View.VISIBLE
                    searchBinding.searchResult.visibility = View.GONE
                }

        }


        val department = intent.getSerializableExtra("department") as? List<DepartmentModel>
        searchBinding.recyclerDep.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        searchBinding.recyclerDep.adapter = DepartmentAdapter(department = department!!,resources)

    }
}