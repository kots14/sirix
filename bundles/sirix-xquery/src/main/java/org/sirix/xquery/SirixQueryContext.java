package org.sirix.xquery;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.update.op.UpdateOp;
import org.brackit.xquery.xdm.Sequence;
import org.sirix.api.xml.XmlNodeTrx;
import org.sirix.xquery.node.XmlDBNode;
import org.sirix.xquery.node.XmlDBStore;

/**
 * Query context for Sirix.
 *
 * @author Johannes
 *
 */
public final class SirixQueryContext extends QueryContext {

  /** Commit strategies. */
  public enum CommitStrategy {
    /** Automatically commit. */
    AUTO,

    /** Explicitly commit (not within the applyUpdates-method). */
    EXPLICIT
  }

  /** The commit strategy. */
  private CommitStrategy mCommitStrategy;

  /**
   * Constructor.
   *
   * @param store the database storage to use
   */
  public SirixQueryContext(final XmlDBStore store) {
    this(store, CommitStrategy.AUTO);
  }

  /**
   * Constructor.
   *
   * @param store the database storage to use
   * @param commitStrategy the commit strategy to use
   */
  public SirixQueryContext(final XmlDBStore store, final CommitStrategy commitStrategy) {
    super(store);
    mCommitStrategy = checkNotNull(commitStrategy);
  }

  @Override
  public void applyUpdates() throws QueryException {
    super.applyUpdates();

    if (mCommitStrategy == CommitStrategy.AUTO) {
      final List<UpdateOp> updateList = getUpdateList() == null
          ? Collections.emptyList()
          : getUpdateList().list();

      if (!updateList.isEmpty()) {
        final Function<Sequence, Optional<XmlNodeTrx>> mapDBNodeToWtx = sequence -> {
          if (sequence instanceof XmlDBNode) {
            return ((XmlDBNode) sequence).getTrx().getResourceManager().getNodeWriteTrx();
          }

          return Optional.empty();
        };

        final Set<Long> trxIDs = new HashSet<>();

        updateList.stream()
                  .map(UpdateOp::getTarget)
                  .map(mapDBNodeToWtx)
                  .flatMap(Optional::stream)
                  .filter(trx -> trxIDs.add(trx.getId()))
                  .forEach(XmlNodeTrx::commit);
      }
    }
  }
}
