import java.io.File;

import javax.sound.midi.*;

public class MidiManager {
    public final static int PIANO = 0,
        GUITAR = 25,
        VIOLIN = 41,
        SYNTH = 81;

    private final int NUM_TRACKS = 4;
    private final int VELOCITY = 64;

    private boolean isRecording;

    private int activeTrack;
    private int activeChannel;

    private Synthesizer synthesizer;
    private Sequencer sequencer;
    private Sequence sequence;

    private Transmitter seqTransmitter;
    private Receiver synthReceiver;

    private Track[] tracks;
    private int[] trackInstruments;

    /**
     * Creates a MidiManager object, by default not recording and with track 0 and channel 0 set to be
     * active
     */

    public MidiManager() {
        isRecording = false;
        activeTrack = 0;
        activeChannel = 0;

        try {
            // create midi devices and the sequence to be recorded to
            synthesizer = MidiSystem.getSynthesizer();
            sequencer = MidiSystem.getSequencer();
            sequence = new Sequence(Sequence.SMPTE_30, 2, NUM_TRACKS); // Sets the recording to 30 frames per second,
                                                                       // and 2 ticks per frame, to line up with the
                                                                       // canvas.animate speed
            // open devices
            synthesizer.open();
            sequencer.open();
            // create receivers and transmitters, and attach things to each other
            synthReceiver = synthesizer.getReceiver();
            seqTransmitter = sequencer.getTransmitter();
            seqTransmitter.setReceiver(synthReceiver);
            // initialize the array of tracks
            tracks = new Track[NUM_TRACKS];
            for (int i = 0; i < NUM_TRACKS; i++) {
                tracks[i] = sequence.createTrack();
            }
            // set all tracks to piano by default
            trackInstruments = new int[NUM_TRACKS];
            for (int i = 0; i < NUM_TRACKS; i++) {
                trackInstruments[i] = PIANO;
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * State management
     */

    /**
     * Deletes the current track at the given index and creates a new track in the sequence with the
     * same index, preserving the instrument selected for the track
     * 
     * @param track the index of the track to be cleared
     */
    public void clearTrack(int track) {
        sequence.deleteTrack(tracks[track]);
        tracks[track] = sequence.createTrack();
        selectInstrument(trackInstruments[activeTrack], 0);
    }

    /**
     * Sets the active track (the one being played on and recorded to) to the given index
     * 
     * @param track index of the track to activate
     */
    public void setActiveTrack(int track) {
        if (track > NUM_TRACKS - 1) {
            throw new IndexOutOfBoundsException();
        }
        activeTrack = track;
        activeChannel = track;
    }

    /**
     * Gets the index of the active track (the one being played on and recorded to)
     *
     * @return the index of the currently active Track
     */
    public int getActiveTrack() {
        return activeTrack;
    }

    /**
     * Checks if a track is empty
     * 
     * @param track track to be checked
     * @return whether track is empty
     */
    public boolean isEmpty(int track) {
        return tracks[track].ticks() == 0;
    }

    /**
     * Sends a message to the active track to change its instrument to the given instrument int as
     * defined by the general MIDI standard
     * 
     * @param instrument int representing the instrument in general MIDI
     * @param tick       the tick in the recording, for mid-recording instrument changes
     */
    public void selectInstrument(int instrument, int tick) {
        ShortMessage msg = makeShortMessage(ShortMessage.PROGRAM_CHANGE, activeChannel, instrument, 0);
        synthReceiver.send(msg, -1);
        trackInstruments[activeTrack] = instrument;
        tracks[activeTrack].add(new MidiEvent(msg, tick));
    }

    /**
     * Getter for the isRecording field
     * 
     * @return whether the app is currently recording
     */
    public boolean isRecording() {
        return isRecording;
    }

    /*
     * Note playing
     */

    /**
     * Plays a note in the active channel at the given pitch with a velocity of 64 on the synthesizer
     * 
     * @param pitch int representing the midi note value
     */
    public void playPitch(int pitch, int tick) {
        ShortMessage msg = makeShortMessage(ShortMessage.NOTE_ON, activeChannel, pitch, VELOCITY);
        synthReceiver.send(msg, -1); // -1 means no time stamp
        if (isRecording) {
            tracks[activeTrack].add(new MidiEvent(msg, tick));
        }
    }

    /**
     * Stops the note playing at the input pitch on the active channel
     * 
     * @param pitch int representing the midi note value
     */
    public void stopPitch(int pitch, int tick) {
        ShortMessage msg = makeShortMessage(ShortMessage.NOTE_OFF, activeChannel, pitch, 0);
        synthReceiver.send(msg, -1); // -1 means no time stamp
        if (isRecording) {
            tracks[activeTrack].add(new MidiEvent(msg, tick));
        }
    }

    /*
     * Playback and recording
     */

    /**
     * Plays the sequence on the sequencer
     */
    public void playSequence() {
        try {
            sequencer.setSequence(sequence);
            sequencer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Pause the currently playing sequence in its current position
     */
    public void pauseSequence() {
        sequencer.stop();
    }

    /**
     * Sets the sequencer position to the beginning of the sequence
     */
    public void setToStart() {
        sequencer.setTickPosition(0);
    }

    /**
     * Start recording to the sequence on the active track
     */
    public void startRecording() {
        if (!isRecording) {
            isRecording = true;
            clearTrack(activeTrack);
            setToStart();
            playSequence();
        }
    }

    /**
     * Stop recording on the active track and return to the beginning of the sequence
     */
    public void stopRecording() {
        if (isRecording) {
            isRecording = false;
            pauseSequence();
            setToStart();
        }
    }

    /**
     * Saves the data in the sequence to a MIDI file
     * 
     * @param filename the name of the file to be saved
     */
    public void saveRecording(String filename) {
        File file = new File(filename + ".mid");
        try {
            MidiSystem.write(sequence, 1, file);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /*
     * Private methods
     */

    /**
     * Helper method to handle exception stuff when creating a midi message; returns null if the input
     * data is invalid
     */
    private ShortMessage makeShortMessage(int arg0, int arg1, int arg2, int arg3) {
        ShortMessage newMsg = null;
        try {
            newMsg = new ShortMessage();
            newMsg.setMessage(arg0, arg1, arg2, arg3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return newMsg;
    }

}