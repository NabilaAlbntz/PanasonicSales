package pogumedia.panasonic.sales.ui.actvity.store.survey.history


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.helper.util.ViewPagerAdapter
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.status.StoreSurveyHistoryStatusFragment
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.search.StoreSurveyHistorySearchActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerFragment
import kotlinx.android.synthetic.main.fragment_store_survey_history.*


/**
 * A simple [Fragment] subclass.
 *
 */
class StoreSurveyHistoryFragment : BaseDaggerFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_survey_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        etSearch.setOnClickListener {
            startActivity(Intent(activity, StoreSurveyHistorySearchActivity::class.java))
        }
        setupViewPager(vpContent)
        stlContent.setViewPager(vpContent)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragment(StoreSurveyHistoryStatusFragment.newInstance(false), getString(R.string.label_survey_history_uploaded_title))
        viewPagerAdapter.addFragment(StoreSurveyHistoryStatusFragment.newInstance(true), getString(R.string.label_survey_history_draft_title))
        viewPager.adapter = viewPagerAdapter
    }

}
