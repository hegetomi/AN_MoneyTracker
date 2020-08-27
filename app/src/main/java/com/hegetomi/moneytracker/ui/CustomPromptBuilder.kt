package com.hegetomi.moneytracker.ui

import android.app.Activity
import uk.co.samuelwall.materialtaptargetprompt.ActivityResourceFinder
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.ResourceFinder
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptOptions


class CustomPromptBuilder

    (resourceFinder: ResourceFinder) : PromptOptions<CustomPromptBuilder>(resourceFinder) {
    private var key: String? = null


    constructor(activity: Activity) : this(ActivityResourceFinder(activity))

    fun setPreferenceKey(key: String?): CustomPromptBuilder {
        this.key = key
        return this
    }

    override fun show(): MaterialTapTargetPrompt? {
        val sharedPreferences =
            this.resourceFinder.context.getSharedPreferences("preferences", 0)
        var prompt: MaterialTapTargetPrompt? = null
        if (key == null || !sharedPreferences.getBoolean(key, false)) {
            prompt = super.show()
            if (prompt != null && key != null) {
                sharedPreferences.edit().putBoolean(key, true).apply()
            }
        }
        return prompt
    }
}