package me.havard.assemblyfun.data;

import me.havard.assemblyfun.R;

/** An enum for the different possible difficulties
 * Created by Havard on 13/09/2015.
 */
public enum Difficulty {

    TUTORIAL(R.string.label_difficulty_tutorial, R.color.label_difficulty_color_tutorial),
    VERY_EASY(R.string.label_difficulty_very_easy, R.color.label_difficulty_color_very_easy),
    EASY(R.string.label_difficulty_easy, R.color.label_difficulty_color_very_easy),
    MEDIUM(R.string.label_difficulty_medium, R.color.label_difficulty_color_very_easy),
    TOUGH(R.string.label_difficulty_tough, R.color.label_difficulty_color_very_easy),
    CHALLENGE(R.string.label_difficulty_tougher, R.color.label_difficulty_color_very_hard),
    DIFFICULT(R.string.label_difficulty_difficult, R.color.label_difficulty_color_very_hard),
    HARD(R.string.label_difficulty_very_hard, R.color.label_difficulty_color_very_hard),
    VERY_HARD(R.string.label_difficulty_very_hard, R.color.label_difficulty_color_very_hard),
    INSANE(R.string.label_difficulty_insane, R.color.label_difficulty_color_very_hard);

    private int label_id, color_id;
    Difficulty(int label_id, int color_id)
    {
        this.label_id = label_id;
        this.color_id = color_id;
    }

    public int getLabelId()
    {
        return label_id;
    }

    public int getColorId()
    {
        return color_id;
    }
}
