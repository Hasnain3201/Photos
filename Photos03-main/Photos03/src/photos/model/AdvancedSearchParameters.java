package photos.model;

import java.time.LocalDate;
import java.util.Map; 


    public class AdvancedSearchParameters {
        private LocalDate startDate;
        private LocalDate endDate;
        private Map<String, String> tags;
        private boolean useAnd;
    
        public AdvancedSearchParameters(LocalDate startDate, LocalDate endDate, Map<String, String> tags, boolean useAnd) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.tags = tags;
            this.useAnd = useAnd;
        }
    

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isUseAnd() {
        return useAnd;
    }

    public Map<String, String> getTags() {
        return tags;
    }

}