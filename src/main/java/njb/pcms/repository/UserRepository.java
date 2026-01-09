package njb.pcms.repository;

import njb.pcms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ユーザーのデータベース操作を行うリポジトリインターフェース
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 指定された学生IDを持つユーザーを検索します。
     * (WHERE student_id = :studentId)
     * 
     * @param studentId 検索する学生ID
     * @return 検索結果のユーザー
     */
    Optional<User> findByStudentId(String studentId);

    /**
     * 指定された役割を持つユーザーを検索します。
     * (WHERE role = :role)
     * 
     * @param role 検索する役割
     * @return 検索結果のユーザーリスト
     */
    List<User> findByRole(User.UserRole role);

    /**
     * 指定された学生ID、名前、かなを含むユーザーを検索します。
     * (WHERE student_id LIKE :studentId OR name LIKE :name OR kana LIKE :kana)
     * 
     * @param studentId 検索する学生ID
     * @param name      検索する名前
     * @param kana      検索するかな
     * @return 検索結果のユーザーリスト
     */
    List<User> findTop10ByStudentIdContainingOrNameContainingOrKanaContaining(
            String studentId,
            String name,
            String kana);

    /**
     * 指定された役割と、学生IDの前方一致で件数をカウントします。
     * 
     * @param role   役割
     * @param prefix 学生IDのプレフィックス
     * @return カウント数
     */
    long countByRoleAndStudentIdStartingWith(User.UserRole role, String prefix);

    /**
     * 指定された役割と、学生IDの前方一致でユーザーを検索します。
     * 
     * @param role   役割
     * @param prefix 学生IDのプレフィックス
     * @return ユーザーリスト
     */
    List<User> findByRoleAndStudentIdStartingWith(User.UserRole role, String prefix);

}
