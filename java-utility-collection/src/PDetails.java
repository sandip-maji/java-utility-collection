@Document(collection = "pdetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDetails {
    @Id
    private String id;
    private String service;
    private String workingId;
    private String pid;
}
