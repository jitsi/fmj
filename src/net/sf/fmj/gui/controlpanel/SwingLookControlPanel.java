package net.sf.fmj.gui.controlpanel;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

import javax.media.*;
import javax.swing.*;
import javax.swing.event.*;

import net.sf.fmj.ejmf.toolkit.util.*;
import net.sf.fmj.gui.customslider.*;
import net.sf.fmj.utility.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public class SwingLookControlPanel extends JPanel implements TimeSource,
        SourcedTimerListener
{
    // TODO: the buttons need to enable/disable based on the controller state
    private static final Logger logger = LoggerSingleton.logger;

    public static final int USE_PLAY_CONTROL = 0x0001;
    public static final int USE_STOP_CONTROL = 0x0002;
    public static final int USE_BACK_CONTROL = 0x0004;
    public static final int USE_FORWARD_CONTROL = 0x0008;
    public static final int USE_NEXT_CONTROL = 0x0010;
    public static final int USE_PREVIOUS_CONTROL = 0x0020;
    public static final int USE_POSITION_CONTROL = 0x0040;
    public static final int USE_POSITION_TEXT = 0x0080;
    public static final int USE_LENGTH_TEXT = 0x0100;
    public static final int USE_VOLUME_CONTROL = 0x0200;
    public static final int USE_MUTE_CONTROL = 0x0400;
    public static final int SINGLE_LINE = 0x0800;

    public static final int DEFAULT_FLAGS = USE_PLAY_CONTROL
            | /* USE_STOP_CONTROL | USE_BACK_CONTROL | USE_FORWARD_CONTROL | */USE_NEXT_CONTROL
            | USE_PREVIOUS_CONTROL | USE_POSITION_CONTROL | USE_LENGTH_TEXT
            | USE_POSITION_TEXT | USE_VOLUME_CONTROL | USE_MUTE_CONTROL;

    private static final boolean USE_STANDARD_SLIDER = true; // true to use a
                                                             // JSlider for the
                                                             // position slider,
                                                             // false to use
                                                             // FmjSlider

    private static int nanosToMillis(long nanos)
    {
        return (int) (nanos / 1000000L);
    }

    private static String nanosToString(long nanos)
    {
        final long seconds = nanos / 1000000000L;
        final long minutes = seconds / 60;
        return "" + zeroPad((int) minutes, 2) + ":"
                + zeroPad((int) (seconds % 60), 2);
    }

    private static String zeroPad(int i, int len)
    {
        String result = Integer.toString(i);
        while (result.length() < len)
            result = "0" + result;
        return result;
    }

    private final int flags;

    private final Skin skin;
    public static final Skin DEFAULT_SKIN = new DefaultSkin();

    private Player player;
    private SourcedTimer controlTimer;
    // Timer will fire every TIMER_TICK milliseconds
    final private static int TIMER_TICK = 250;
    private JButton playButton = null;
    private JButton stopButton = null;
    private JButton backButton = null;
    private JButton forwardButton = null;
    private JButton nextButton = null;
    private JButton previousButton = null;
    private JPanel positionPanel = null;
    private JSlider positionSlider = null;
    private JPanel buttonPanel = null;
    private JLabel positionLabel = null;
    private JLabel lengthLabel = null;

    private JPanel audioPanel = null;

    private JSlider volumeSlider = null;

    private JToggleButton muteButton = null;

    /** controller listener to listen to controller events from the player */
    private ControllerListener controllerListener = new ControllerListener()
    {
        public void controllerUpdate(ControllerEvent event)
        {
            logger.fine("Got controller event: " + event);

            final Player player = (Player) event.getSourceController();

            if (player != SwingLookControlPanel.this.player)
                return; // ignore messages from old players.

            // TODO: handle RestartingEvent
            if (event instanceof RealizeCompleteEvent)
            {
                // controller realized
            } else if (event instanceof ResourceUnavailableEvent)
            {
            } else if (event instanceof StopEvent)
            {
                final TransportControlState transportControlState = new TransportControlState();
                transportControlState.setAllowPlay(true);
                transportControlState.setAllowStop(false);
                transportControlState
                        .setAllowVolume(player.getGainControl() != null);

                SwingLookControlPanel.this.onStateChange(transportControlState);

            } else if (event instanceof StartEvent)
            {
                final TransportControlState transportControlState = new TransportControlState();
                transportControlState.setAllowPlay(false);
                transportControlState.setAllowStop(true);
                transportControlState
                        .setAllowVolume(player.getGainControl() != null);

                SwingLookControlPanel.this.onStateChange(transportControlState);
            } else if (event instanceof ControllerErrorEvent)
            {
            } else if (event instanceof ControllerClosedEvent)
            {
            }

            // Slider-related: start/stop timer, etc:
            // if (isOperational())
            {
                if (event instanceof StartEvent
                        || event instanceof RestartingEvent)
                {
                    SwingLookControlPanel.this.onDurationChange(player
                            .getDuration().getNanoseconds());
                    SwingLookControlPanel.this.onProgressChange(getTime());
                    controlTimer.start();
                } else if (event instanceof StopEvent
                        || event instanceof ControllerErrorEvent)
                {
                    controlTimer.stop();
                } else if (event instanceof MediaTimeSetEvent)
                {
                    SwingLookControlPanel.this.onDurationChange(player
                            .getDuration().getNanoseconds()); // just in case

                    // This catches any direct setting of media time
                    // by application. Additionally, it catches
                    // setMediaTime(0) by StandardStopControl.
                    SwingLookControlPanel.this.onProgressChange(getTime());
                }
            }

        }
    };

    boolean playButtonIsPause;

    // TODO: make sure there are no race conditions with
    // suppressProgressChangeNotification and sliderDragInProgress
    // TODO: make sure that after a drag is done we update to the correct
    // position
    // TODO: should positionLabel get updated during a slider drag?
    private volatile boolean suppressProgressChangeNotification = false; // to
                                                                         // prevent
                                                                         // us
                                                                         // from
                                                                         // treating
                                                                         // a
                                                                         // progress
                                                                         // change
                                                                         // to
                                                                         // the
                                                                         // slider
                                                                         // generated
                                                                         // internally
                                                                         // like
                                                                         // a
                                                                         // user
                                                                         // one

    private volatile boolean sliderDragInProgress = false; // prevent us from
                                                           // moving the slider
                                                           // while the user is
                                                           // dragging it

    public SwingLookControlPanel()
    {
        this(DEFAULT_FLAGS, DEFAULT_SKIN);
    }

    public SwingLookControlPanel(int flags, Skin skin)
    {
        super();
        this.flags = flags;
        this.skin = skin;
        initialize();
    }

    public SwingLookControlPanel(int flags, Skin skin, Player player)
    {
        super();
        this.flags = flags;
        this.skin = skin;
        initialize();
        setPlayer(player);
    }

    public SwingLookControlPanel(Player player)
    {
        this(DEFAULT_FLAGS, DEFAULT_SKIN, player);
    }

    /**
     * This method initializes audioPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getAudioPanel()
    {
        if ((flags & USE_VOLUME_CONTROL) == 0)
            return null;

        if (audioPanel == null)
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints.weightx = 1.0;
            audioPanel = new JPanel();
            audioPanel.setLayout(new GridBagLayout());
            audioPanel.setOpaque(false);
            audioPanel.add(getVolumeSlider(), gridBagConstraints);
            if (getMuteButton() != null)
                audioPanel.add(getMuteButton(), new GridBagConstraints());
        }
        return audioPanel;
    }

    /**
     * This method initializes backButton
     *
     * @return javax.swing.JButton
     */
    private JButton getBackButton()
    {
        if ((flags & USE_BACK_CONTROL) == 0)
            return null;

        if (backButton == null)
        {
            backButton = new JButton();
            // backButton.setText("back");
            backButton.setOpaque(false);
            backButton.setIcon(skin.getRewindIcon());
            backButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setRate(-2.0f);
                }
            });
            backButton.setEnabled(false);
        }
        return backButton;
    }

    /**
     * This method initializes buttonPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel()
    {
        if (buttonPanel == null)
        {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.setOpaque(false);
            if (getPreviousButton() != null)
                buttonPanel.add(getPreviousButton(), new GridBagConstraints());
            if (getBackButton() != null)
                buttonPanel.add(getBackButton(), new GridBagConstraints());
            if (getStopButton() != null)
                buttonPanel.add(getStopButton(), new GridBagConstraints());
            if (getPlayButton() != null)
                buttonPanel.add(getPlayButton(), new GridBagConstraints());
            if (getForwardButton() != null)
                buttonPanel.add(getForwardButton(), new GridBagConstraints());
            if (getNextButton() != null)
                buttonPanel.add(getNextButton(), new GridBagConstraints());
        }
        return buttonPanel;
    }

    /**
     * This method is used as a divisor to convert getTime to seconds.
     */
    public long getConversionDivisor()
    {
        return TimeSource.NANOS_PER_SEC;
    }

    /**
     * This method initializes forwardButton
     *
     * @return javax.swing.JButton
     */
    private JButton getForwardButton()
    {
        if ((flags & USE_FORWARD_CONTROL) == 0)
            return null;

        if (forwardButton == null)
        {
            forwardButton = new JButton();
            // forwardButton.setText("forward");
            forwardButton.setOpaque(false);
            forwardButton.setIcon(skin.getFastForwardIcon());
            forwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setRate(2.0f);
                }
            });
            forwardButton.setEnabled(false);
        }
        return forwardButton;
    }

    /**
     * This method initializes muteButton
     *
     * @return javax.swing.JToggleButton
     */
    private JToggleButton getMuteButton()
    {
        if ((flags & USE_VOLUME_CONTROL) == 0)
            return null;

        if ((flags & USE_MUTE_CONTROL) == 0)
            return null;

        if (muteButton == null)
        {
            muteButton = new JToggleButton();
            muteButton.setIcon(skin.getMuteOffIcon());
            muteButton.setOpaque(false);
            muteButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    setMute(muteButton.isSelected());
                    muteButton.setIcon(muteButton.isSelected() ? skin
                            .getMuteOnIcon() : skin.getMuteOffIcon());
                }

            });
        }
        return muteButton;
    }

    /**
     * This method initializes nextButton
     *
     * @return javax.swing.JButton
     */
    private JButton getNextButton()
    {
        if ((flags & USE_NEXT_CONTROL) == 0)
            return null;

        if (nextButton == null)
        {
            nextButton = new JButton();
            // nextButton.setText("next");
            nextButton.setIcon(skin.getStepForwardIcon());
            nextButton.setOpaque(false);
            nextButton.setEnabled(false);
        }
        return nextButton;
    }

    /**
     * This method initializes playButton
     *
     * @return javax.swing.JButton
     */
    private JButton getPlayButton()
    {
        if ((flags & USE_PLAY_CONTROL) == 0)
            return null;
        if (playButton == null)
        {
            playButton = new JButton();
            // playButton.setText("play");
            playButton.setOpaque(false);
            playButton.setIcon(skin.getPlayIcon());
            playButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (playButtonIsPause)
                        pause();
                    else
                    {
                        setRate(1.0f); // TODO: necessary?
                        start();
                    }
                }
            });
            playButton.setEnabled(false);
        }
        return playButton;
    }

    /**
     * This method initializes positionPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPositionPanel()
    {
        if ((flags & USE_POSITION_CONTROL) == 0)
            return null;

        if (positionPanel == null)
        {
            if ((flags & USE_LENGTH_TEXT) != 0)
            {
                lengthLabel = new JLabel();
                lengthLabel.setText(nanosToString(0));
                lengthLabel.setOpaque(false);
            }

            if ((flags & USE_POSITION_TEXT) != 0)
            {
                positionLabel = new JLabel();
                positionLabel.setText(nanosToString(0));
                positionLabel.setOpaque(false);
            }
            positionPanel = new JPanel();
            positionPanel.setOpaque(false);
            positionPanel.setLayout(new BorderLayout());
            positionPanel.add(getPositionSlider(), BorderLayout.CENTER);
            if (positionPanel != null)
                positionPanel.add(positionLabel, BorderLayout.WEST);
            if (lengthLabel != null)
                positionPanel.add(lengthLabel, BorderLayout.EAST);
        }
        return positionPanel;
    }

    /**
     * This method initializes positionSlider
     *
     * @return javax.swing.JSlider
     */
    private JSlider getPositionSlider()
    {
        if ((flags & USE_POSITION_CONTROL) == 0)
            return null;

        if (positionSlider == null)
        {
            positionSlider = USE_STANDARD_SLIDER ? new JSlider()
                    : new CustomSlider();
            positionSlider.setOpaque(false);
            positionSlider.setValue(0);
            positionSlider.setMinimum(0);
            positionSlider.setMaximum(0);
            positionSlider.setEnabled(false);
            positionSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                { // note: this gets called whether the user updates it, or
                  // whether it is updated automatically.

                    if (suppressProgressChangeNotification)
                        return;

                    final JSlider source = (JSlider) e.getSource();
                    sliderDragInProgress = source.getValueIsAdjusting();
                    if (source.getValueIsAdjusting())
                        return; // we only care about "final" positions the user
                                // has adjusted to.
                    final int valueMillis = source.getValue();
                    logger.fine("User adjusted position slider to (millis): "
                            + valueMillis + " from " + source.getMinimum()
                            + "-" + source.getMaximum());

                    player.setMediaTime(new Time(valueMillis / 1000.0)); // TODO:
                                                                         // use
                                                                         // nanos
                }

            });
        }
        return positionSlider;
    }

    /**
     * This method initializes previousButton
     *
     * @return javax.swing.JButton
     */
    private JButton getPreviousButton()
    {
        if ((flags & USE_PREVIOUS_CONTROL) == 0)
            return null;

        if (previousButton == null)
        {
            previousButton = new JButton();
            previousButton.setOpaque(false);
            // previousButton.setText("previous");
            previousButton.setIcon(skin.getStepBackwardIcon());
            previousButton.setEnabled(false);
        }
        return previousButton;
    }

    /**
     * This method initializes stopButton
     *
     * @return javax.swing.JButton
     */
    private JButton getStopButton()
    {
        if ((flags & USE_STOP_CONTROL) == 0)
            return null;

        if (stopButton == null)
        {
            stopButton = new JButton();
            // stopButton.setText("stop");
            stopButton.setOpaque(false);
            stopButton.setIcon(skin.getStopIcon());
            stopButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    stop();
                }
            });
            stopButton.setEnabled(false);
        }
        return stopButton;
    }

    // For TimeSource interface
    /**
     * As part of TimeSource interface, getTime returns the current media time
     * in nanoseconds.
     */
    public long getTime()
    {
        if (player == null)
            return 0L;
        return player.getMediaNanoseconds();
    }

    /**
     * This method initializes volumeSlider
     *
     * @return javax.swing.JSlider
     */
    private JSlider getVolumeSlider()
    {
        if ((flags & USE_VOLUME_CONTROL) == 0)
            return null;

        if (volumeSlider == null)
        {
            volumeSlider = new JSlider();
            volumeSlider.setMinimum(0);
            volumeSlider.setMaximum(100);
            volumeSlider.setValue(70);
            volumeSlider.setPreferredSize(new Dimension(100, 29));
            volumeSlider.setOpaque(false);
            volumeSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!volumeSlider.getValueIsAdjusting())
                    {
                        float newValue = volumeSlider.getValue() / 100.0f;
                        setGain(newValue);
                    }
                }
            });
        }
        return volumeSlider;
    }

    /**
     * This method initializes this
     *
     */
    private void initialize()
    {
        if ((flags & SINGLE_LINE) != 0)
        {
            this.setLayout(new BorderLayout());
            this.setSize(new Dimension(553, 58));

            if (getButtonPanel() != null)
                this.add(getButtonPanel(), BorderLayout.WEST);
            if (getPositionPanel() != null)
                this.add(getPositionPanel(), BorderLayout.CENTER);
            if (getAudioPanel() != null)
                this.add(getAudioPanel(), BorderLayout.EAST);
        } else
        {
            this.setLayout(new BorderLayout());
            this.setSize(new Dimension(553, 58));

            if (getPositionPanel() != null)
                this.add(getPositionPanel(), BorderLayout.NORTH);
            if (getButtonPanel() != null)
                this.add(getButtonPanel(), BorderLayout.WEST);
            if (getAudioPanel() != null)
                this.add(getAudioPanel(), BorderLayout.EAST);
        }
        setAudioControlEnabled(false);
    }

    public void onDurationChange(long nanos)
    {
        if (nanos == Duration.DURATION_UNKNOWN.getNanoseconds()
                || nanos == Duration.DURATION_UNBOUNDED.getNanoseconds())
        {
            if (positionSlider != null)
            {
                suppressProgressChangeNotification = true;
                positionSlider.setEnabled(false);
                suppressProgressChangeNotification = false;
            }
            if (lengthLabel != null)
                lengthLabel.setText("");
        } else
        {
            if (positionSlider != null)
            {
                suppressProgressChangeNotification = true;
                positionSlider.setEnabled(true);
                positionSlider.setMinimum(0);
                positionSlider.setMaximum(nanosToMillis(nanos)); // millis is
                                                                 // good enough.
                suppressProgressChangeNotification = false;
            }
            if (lengthLabel != null)
                lengthLabel.setText(nanosToString(nanos));
        }
    }

    public void onProgressChange(long nanos)
    {
        if (positionSlider != null)
        {
            if (positionSlider.isEnabled())
            {
                if (!sliderDragInProgress)
                {
                    suppressProgressChangeNotification = true;
                    positionSlider.setValue(nanosToMillis(nanos));
                    suppressProgressChangeNotification = false;
                }
            }
        }
        if (positionLabel != null)
            positionLabel.setText(nanosToString(nanos));

    }

    public void onStateChange(TransportControlState state)
    {
        if (getStopButton() != null)
            getStopButton().setEnabled(state.isAllowStop());
        if (getPlayButton() != null)
        {
            if (state.isAllowPlay() || state.isAllowStop())
            {
                if (state.isAllowStop())
                {
                    getPlayButton().setIcon(skin.getPauseIcon());
                    playButtonIsPause = true;
                } else
                {
                    getPlayButton().setIcon(skin.getPlayIcon());
                    playButtonIsPause = false;
                }
                getPlayButton().setEnabled(
                        state.isAllowPlay() || state.isAllowStop());
            }
        }
        setAudioControlEnabled(state.isAllowVolume());
    }

    private void pause()
    {
        if (player != null)
        {
            player.stop();
            // player.setPosition(0);
        }
    }

    public void setAudioControlEnabled(boolean enabled)
    {
        if (getVolumeSlider() != null)
            getVolumeSlider().setEnabled(enabled);
        if (getMuteButton() != null)
            getMuteButton().setEnabled(enabled);
    }

    private void setGain(float gain)
    {
        if (player != null && player.getGainControl() != null)
        {
            player.getGainControl().setLevel(gain);
        }
    }

    private void setMute(boolean mute)
    {
        if (player != null && player.getGainControl() != null)
        {
            player.getGainControl().setMute(mute);
        }
    }

    public void setPlayer(Player player)
    {
        this.player = player;

        final TransportControlState transportControlState = new TransportControlState();
        if (player.getState() == Controller.Started)
        {
            transportControlState.setAllowPlay(false);
            transportControlState.setAllowStop(true);
            transportControlState
                    .setAllowVolume(player.getGainControl() != null);
        } else
        {
            transportControlState.setAllowPlay(true);
            transportControlState.setAllowStop(false);
            transportControlState.setAllowVolume(false); // can't get gain
                                                         // control on
                                                         // unrealized player
        }

        if (getPreviousButton() != null)
            getPreviousButton().setEnabled(true);
        if (getNextButton() != null)
            getNextButton().setEnabled(true);
        if (getForwardButton() != null)
            getForwardButton().setEnabled(true);
        if (getBackButton() != null)
            getBackButton().setEnabled(true);

        onStateChange(transportControlState);

        // Setup timer
        controlTimer = new SourcedTimer(this, TIMER_TICK);
        controlTimer.addSourcedTimerListener(this);

        if (player.getState() == Controller.Started)
        {
            onDurationChange(player.getDuration().getNanoseconds());

            controlTimer.start(); // this handles the case where it is already
                                  // started before we get here, in which case
                                  // controllerUpdate will never get called with
                                  // the initial state
        }

        player.addControllerListener(controllerListener);
    }

    private void setRate(float rate)
    {
        if (player != null)
        {
            player.setRate(rate);
        }
    }

    private void start()
    {
        if (player != null)
        {
            player.start();
        }
    }

    private void stop()
    {
        if (player != null)
        {
            player.stop();
            player.setMediaTime(new Time(0));
        }
    }

    /**
     * This method implements the SourcedTimerListener interface. Each timer
     * tick causes slider thumbnail to move if a ProgressBar was built for this
     * control panel.
     *
     * @see net.sf.fmj.ejmf.toolkit.util.SourcedTimer
     */
    public void timerUpdate(SourcedTimerEvent e)
    {
        // Since we are also the TimeSource, we can get
        // directly from StandardControls instance.
        // Normally, one would call e.getTime().

        onProgressChange(getTime());

    }
}

class TransportControlState
{
    private boolean allowStop;
    private boolean allowPlay;
    private boolean allowVolume;

    public boolean isAllowPlay()
    {
        return allowPlay;
    }

    public boolean isAllowStop()
    {
        return allowStop;
    }

    public boolean isAllowVolume()
    {
        return allowVolume;
    }

    public void setAllowPlay(boolean allowPlay)
    {
        this.allowPlay = allowPlay;
    }

    public void setAllowStop(boolean allowStop)
    {
        this.allowStop = allowStop;
    }

    public void setAllowVolume(boolean allowVolume)
    {
        this.allowVolume = allowVolume;
    }

}
