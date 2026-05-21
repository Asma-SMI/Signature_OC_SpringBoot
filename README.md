<<<<<<< HEAD
# Signature_OC_SpringBoot
=======
# MS-OC - Microservice Obligations Cautionnees

Projet Spring Boot 3 / Java 17 compatible avec l'architecture cible MS-OC.

## Responsabilites

MS-OC est le microservice metier. Il ne fait pas de parsing XML, pas de signature et pas d'orchestration workflow.

Il gere :

- consommation Kafka entrante depuis la station d'echange ;
- creation du dossier `OC_FLOW` ;
- creation des details metier `OC_FLOW_DETAIL` ;
- creation de l'operation `OC_OPERATION` ;
- decisions downstream MS-WF avec `finalize=true/false` ;
- audit metier ;
- snapshots JSON ;
- Outbox Kafka sortante vers la station.

## Lancer en dev

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

En profil `dev`, la securite est ouverte et un endpoint de simulation Kafka est actif.

## Simuler un flux entrant sans station d'echange

```http
POST http://localhost:8082/api/oc/dev/kafka/inbound
Content-Type: application/json
```

Body : utiliser `src/test/resources/kafka/inbound/oc-inbound-valid-o06.json`.

Cela publie dans le topic :

```text
station.oc.inbound
```

Puis le consumer MS-OC traite le message et alimente :

```text
OC_INBOUND_EVENT
OC_FLOW
OC_FLOW_DETAIL
OC_OPERATION
OC_FLOW_PAYLOAD
```

## Tester une decision intermediaire

```http
POST http://localhost:8082/api/oc/flows/88276218/decision?finalize=false
X-User-Id: 202
X-Role-Code: CONTROLEUR_1
X-Org-Node-Id: AGENCE_001
X-Correlation-Id: CORR-OC-0001
Idempotency-Key: IDEM-OC-0001
Content-Type: application/json

{
  "decision": "PROGRESS",
  "reason": null,
  "comment": "Premier controle effectue"
}
```

Resultat attendu : operation `IN_PROGRESS`, dossier non impacte, pas d'Outbox.

## Tester une decision finale

```http
POST http://localhost:8082/api/oc/flows/88276218/decision?finalize=true
X-User-Id: 203
X-Role-Code: CONTROLEUR_2
X-Org-Node-Id: AGENCE_001
X-Correlation-Id: CORR-OC-0001
Idempotency-Key: IDEM-OC-0002
Content-Type: application/json

{
  "decision": "ACCEPT",
  "reason": null,
  "comment": "Dossier conforme"
}
```

Resultat attendu : operation `VALIDATED`, dossier `ACCEPTED`, creation d'un `OC_OUTBOUND_EVENT` en `PENDING`, puis publication vers Kafka par le publisher Outbox.

## Endpoints consultatifs

```text
GET /api/oblig-caut/flows
GET /api/oblig-caut/flows/{businessKey}
GET /api/oblig-caut/flows/{businessKey}/audit
GET /api/oblig-caut/flows/{businessKey}/payloads
```

## Important

Le fichier SQL Flyway est fourni dans :

```text
src/main/resources/db/migration/V1__create_ms_oc_schema.sql
```

Par defaut `spring.flyway.enabled=false` pour eviter une execution accidentelle sur une base Oracle existante.
>>>>>>> 07d9d9e (v1)
