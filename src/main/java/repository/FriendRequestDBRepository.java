package repository;
import domain.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class FriendRequestDBRepository implements Repository<Long , FriendRequest> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User>  userRepo;

    public FriendRequestDBRepository(String url, String username, String password, Repository<Long, User> userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        String sql = "INSERT INTO friend_request (from_user_id, to_user_id, status, date) VALUES (?, ?, ?, ?)";
        try( Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        )
        {
            ps.setLong(1 , entity.getFrom().getId());
            ps.setLong(2 , entity.getTo().getId());
            ps.setString(3 , entity.getStatus().toString());
            ps.setTimestamp(4 , Timestamp.valueOf(entity.getDate()));



            ps.executeUpdate();
            try(ResultSet generatedKey = ps.getGeneratedKeys()){
                if(generatedKey.next()){
                    entity.setId(generatedKey.getLong(1));
                }
           }

            return null;
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return entity;
    }

    @Override
    public List<FriendRequest> findAll()
    {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM friend_request";
        try (Connection connection = DriverManager.getConnection(url , username , password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
                Long id = rs.getLong("id");
                Long fromID = rs.getLong("from_user_id");
                Long toID = rs.getLong("to_user_id");
                String Status = rs.getString("status");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();

                User from = userRepo.findOne(fromID);
                User to = userRepo.findOne(toID);
                RequestType status =  RequestType.valueOf(Status);

                FriendRequest request = new FriendRequest(from , to  , status, date);
                request.setId(id);
                requests.add(request);
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return requests;
    }

    @Override
    public FriendRequest findOne(Long id)
    {
        String sql = "SELECT * FROM friend_request WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username , password);
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                Long fromid = rs.getLong("from_user_id");
                Long toid = rs.getLong("to_user_id");
                String Status = rs.getString("status");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();

                User from = userRepo.findOne(fromid);
                User to = userRepo.findOne(toid);

                RequestType status = RequestType.valueOf(Status);

                FriendRequest request = new  FriendRequest(from,to,status,date);
                request.setId(id);
                return request;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FriendRequest delete(Long aLong) { return null; }

    @Override
    public FriendRequest update(FriendRequest entity)
    {
        String sql = "UPDATE friend_request SET status = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username , password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1 , entity.getStatus().toString());
            ps.setLong(2 , entity.getId());

            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
    }
}