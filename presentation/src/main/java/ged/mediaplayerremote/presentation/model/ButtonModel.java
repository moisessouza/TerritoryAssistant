package ged.mediaplayerremote.presentation.model;

/**
 * Class representing a button with associated MPC-HC api command. If command code equals 0, the button is disabled and
 * will not be rendered.
 */
public class ButtonModel
{
    private int commandCode;
    private String description;

    public ButtonModel(int commandCode, String description)
    {
        this.commandCode = commandCode;
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getCommandCode()
    {
        return commandCode;
    }

    public void setCommandCode(int commandCode)
    {
        this.commandCode = commandCode;
    }

}
