package UserInterface;

import DynamicBandwidthManagement.GeneratorModel;
import DynamicBandwidthManagement.GeneratorInterface;

public class Main {
    public static void main(String[] args) {
        GeneratorInterface DBG_model = new GeneratorModel();
        ControllerInterface controller = new UIController(DBG_model);
    }
}

