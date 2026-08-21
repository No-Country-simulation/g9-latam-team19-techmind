package com.techmind.backend.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

public class ValidationGroups {
    // Grupo 1: Para verificar presencia/vacíos
    public interface PrimerGrupo {}

    // Grupo 2: Para verificar formato (Regex)
    public interface SegundoGrupo {}

    // Define la secuencia estricta de ejecución: PrimerGrupo -> SegundoGrupo -> Default
    @GroupSequence({PrimerGrupo.class, SegundoGrupo.class, Default.class})
    public interface SecuenciaOrdenada {}
}
