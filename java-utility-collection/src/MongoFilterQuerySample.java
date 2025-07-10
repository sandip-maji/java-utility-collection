public class MongoFilterQuerySample {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<PDetails> filterPDetails(String filterQuery, int page, int size) {
        Map<String, List<String>> filterMap = parseFilterQuery(filterQuery);

        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();

        if (filterMap.containsKey("service")) {
            criteriaList.add(Criteria.where("service").in(filterMap.get("service")));
        }
        if (filterMap.containsKey("pid")) {
            criteriaList.add(Criteria.where("pis").in(filterMap.get("pis")));
        }
        if (filterMap.containsKey("workingId")) {
            criteriaList.add(Criteria.where("workingId").in(filterMap.get("workingId")));
        }

        if (!criteriaList.isEmpty()) {
            criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, PDetails.class);

        query.with(PageRequest.of(page, size));
        List<PDetails> results = mongoTemplate.find(query, PDetails.class);

        return new PageImpl<>(results, PageRequest.of(page, size), total);
    }


    private Map<String, List<String>> parseFilterQuery(String filterQuery) {
        Map<String, List<String>> map = new HashMap<>();

        if (filterQuery == null || filterQuery.isBlank()) return map;

        String[] parts = filterQuery.split("\\|");
        for (String part : parts) {
            String[] keyVal = part.split(":");
            if (keyVal.length == 2) {
                String key = keyVal[0].trim();
                String val = keyVal[1].replaceAll("[\\[\\]]", ""); // remove square brackets
                List<String> values = Arrays.stream(val.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                map.put(key, values);
            }
        }

        return map;
    }





}