package pe.edu.cibertec.rueditas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.rueditas.dto.ConsultaRequestDTO;
import pe.edu.cibertec.rueditas.dto.ConsultaResponseDTO;
import pe.edu.cibertec.rueditas.viewmodel.ConsultaModel;

@Controller
@RequestMapping()
public class ConsultaController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/")
    public String inicio(Model model) {
        ConsultaModel consultaModel = new ConsultaModel("00", "");
        model.addAttribute("ConsultaModel", consultaModel);
        return "inicio";
    }

    @PostMapping("/buscar")
    public String auntenticar(@RequestParam("numPlaca") String numPlaca,
                              Model model) {
        System.out.println("auntenticar.init " + numPlaca);
        if (numPlaca.length() != 8){

            System.out.println("auntenticar.LengthError " + numPlaca);
            ConsultaModel consultaModel = new ConsultaModel("403", "Error: El campo debe tener una longitud 8 fija");
            model.addAttribute("ConsultaModel", consultaModel);
            return "inicio";
        }

        try {
            System.out.println("auntenticar.request.cars " + numPlaca);
            String endpoint = "http://localhost:8080/getCars";
            ConsultaRequestDTO consultaRequestDTO = new ConsultaRequestDTO(numPlaca);
            ConsultaResponseDTO responseDTO = restTemplate.postForObject(endpoint,consultaRequestDTO,ConsultaResponseDTO.class);
            System.out.println("loginResponseDTO "+consultaRequestDTO);
            assert responseDTO != null;
            if (!(responseDTO.codigo().equals("200"))){

                ConsultaModel consultaModel = new ConsultaModel("404", "Error: No se encontró un vehículo para la placa ingresada");
                model.addAttribute("ConsultaModel", consultaModel);
                return "inicio";
            }
            ConsultaResponseDTO consultaResponseDTO = new ConsultaResponseDTO("200", "ok", responseDTO.marca(), responseDTO.modelo(), responseDTO.nroAsientos(),responseDTO.precio(),responseDTO.color());
            model.addAttribute("ConsultaModel", consultaResponseDTO);
            return "principal";
        }catch (Exception e){
            System.out.println("auntenticar.err " +  e.getMessage());

            ConsultaModel consultaModel = new ConsultaModel("500", "Err: " + e.getMessage());
            model.addAttribute("loginModel", consultaModel);
            return "inicio";
        }
    }
}
