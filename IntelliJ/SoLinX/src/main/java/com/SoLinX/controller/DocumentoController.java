package com.SoLinX.controller;

import com.SoLinX.dto.DocumentoDto;
import com.SoLinX.model.Documento;
import com.SoLinX.service.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/SoLinX/api")
@RestController
@AllArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @PostMapping("/documento/{boleta}/{periodo}")
    public ResponseEntity<DocumentoDto> subirDocumento(
            @PathVariable("boleta") Integer boleta,
            @PathVariable("periodo") Integer periodo,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            Documento doc = documentoService.subirDocumento(
                    boleta,
                    periodo,
                    archivo.getBytes(),
                    archivo.getOriginalFilename()
            );
            return ResponseEntity.ok(convertToDto(doc));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/documento/{boleta}")
    public ResponseEntity<List<DocumentoDto>> getByBoleta(@PathVariable("boleta") Integer boleta) {
        List<Documento> documentos = documentoService.getByBoleta(boleta);
        if (documentos.isEmpty()) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(documentos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/documento/{boleta}/{periodo}/archivo")
    public ResponseEntity<byte[]> descargarDocumento(
            @PathVariable("boleta") Integer boleta,
            @PathVariable("periodo") Integer periodo) {
        Documento doc = documentoService.getByBoletaAndPeriodo(boleta, periodo);
        if (doc == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(doc.getArchivo());
    }

    private DocumentoDto convertToDto(Documento doc) {
        return DocumentoDto.builder()
                .idDocumento(doc.getIdDocumento())
                .periodo(doc.getPeriodo())
                .nombreArchivo(doc.getNombreArchivo())
                .fechaSubida(doc.getFechaSubida() != null ? sdf.format(doc.getFechaSubida()) : null)
                .boleta(doc.getEstudiante().getBoleta())
                .build();
    }
}