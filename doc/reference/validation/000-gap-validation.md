# Gap Analysis Validation Checklist

Please note 12 services implented at impl/gifting-X with API documentation located at impl/gifting-X/src/main/resources/openapi.yaml (gifting-common is not a service)

Repeat for each service X in aplhabetical order {
   1. Read and understand every line of CHARTER.md
   2. Read and understand every line of impl/gifting-X/src/main/resources/openapi.yaml
   3. Repeat for each documentd gap Y in doc/features/000-gap-validation.md{
      if Y is consistant with CHARTER {
         if Y alignes with doc/governance/GOVERNANCE-SUMMARY.md{
           document a task to do Y in doc/services/X/ToDo.md consistant with governance.
         }
      } // Analyze next Y against this X
   } // Analyze next X against
}