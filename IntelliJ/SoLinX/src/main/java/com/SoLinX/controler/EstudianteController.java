package com.SoLinX.controler;

import com.SoLinX.dto.EstudianteDto;
import com.SoLinX.model.Estudiante;
import com.SoLinX.service.EstudianteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class EstudianteController {
    private final EstudianteService estudianteService;
    private List<EstudianteDto> EstudianteDtos;

    public void loadList() {
        EstudianteDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EstudianteDtos.add(
                    EstudianteDto.builder()
                            .boleta(i++)
                            .carrera("Carrera " + i)
                            .escuela("Escuela " + i)
                            .build()
            );
        }
    }

    @RequestMapping("/estudiante")
    public ResponseEntity<List<EstudianteDto>> lista() {
        List<Estudiante> Estudiantes = estudianteService.getAll();
        if(Estudiantes == null || Estudiantes.size()== 0) {
            return ResponseEntity.notFound( ).build( );
        } return ResponseEntity
                .ok(
                        Estudiantes
                                .stream()
                                .map(u -> EstudianteDto.builder()
                                        .boleta(u.getBoleta())
                                        .carrera(u.getCarrera())
                                        .escuela(u.getEscuela())
                                        .build())
                                .collect(Collectors.toList()));
    }

    @RequestMapping("/estudiante/{id}")
    public ResponseEntity<EstudianteDto>getById(@PathVariable Integer boleta) {
        Estudiante u = estudianteService.getById(boleta);

        if(u == null ) {
            return  ResponseEntity.notFound().build();
        } return ResponseEntity.ok(EstudianteDto.builder()
                .boleta(u.getBoleta())
                .carrera(u.getCarrera())
                .escuela(u.getEscuela())
                .build());
    }

    @PostMapping( "/estudiante")
    public ResponseEntity<EstudianteDto> save(@RequestBody EstudianteDto EstudianteDto) {
        Estudiante u = Estudiante.
                builder()
                .boleta( EstudianteDto.getBoleta())
                .carrera( EstudianteDto.getCarrera())
                .escuela(EstudianteDto.getEscuela())
                .build();
        estudianteService.save(u);
        return ResponseEntity.ok(EstudianteDto.builder()
                .boleta(u.getBoleta())
                .carrera(u.getCarrera())
                .escuela(u.getEscuela())
                .build());
    }

    @DeleteMapping( "/estudiante/{id}")
    public ResponseEntity<EstudianteDto> delete(@PathVariable Integer id) {
        estudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping( "/estudiante/{id}")
    public ResponseEntity<EstudianteDto>update( @PathVariable Integer id, @RequestBody EstudianteDto EstudianteDto) {
        Estudiante aux = estudianteService.update( id, Estudiante
                .builder()
                .boleta( EstudianteDto.getBoleta())
                .carrera( EstudianteDto.getCarrera())
                .escuela(EstudianteDto.getEscuela())
                .build());
        return ResponseEntity.ok(EstudianteDto.builder()
                .boleta( aux.getBoleta())
                .carrera(aux.getCarrera())
                .escuela(aux.getEscuela())
                .build());
    }
}
