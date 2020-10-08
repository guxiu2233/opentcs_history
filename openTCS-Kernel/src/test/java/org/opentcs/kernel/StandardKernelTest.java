/*
 *
 * Created on September 20, 2006, 9:21 AM
 */
package org.opentcs.kernel;

import com.google.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.opentcs.access.Kernel;
import org.opentcs.access.LocalKernel;
import org.opentcs.util.eventsystem.EventHub;
import org.opentcs.util.eventsystem.TCSEvent;

/**
 * A test case for StandardKernel.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class StandardKernelTest {

  /**
   * The kernel instance to be tested.
   */
  private LocalKernel kernel;

  private KernelState kernelStateShutdown;
  private KernelState kernelStateModelling;
  private KernelState kernelStateOperating;

  @Before
  public void setUp() {
    @SuppressWarnings("unchecked")
    EventHub<TCSEvent> eventHub = mock(EventHub.class);

    // Build a map of providers for our mocked state objects.
    Map<Kernel.State, Provider<KernelState>> stateMap = new HashMap<>();
    kernelStateShutdown = mock(KernelState.class);
    when(kernelStateShutdown.getState()).thenReturn(Kernel.State.SHUTDOWN);
    stateMap.put(Kernel.State.SHUTDOWN,
                 new KernelStateProvider(kernelStateShutdown));
    
    kernelStateModelling = mock(KernelState.class);
    when(kernelStateModelling.getState()).thenReturn(Kernel.State.MODELLING);
    stateMap.put(Kernel.State.MODELLING,
                 new KernelStateProvider(kernelStateModelling));
    
    kernelStateOperating = mock(KernelState.class);
    when(kernelStateOperating.getState()).thenReturn(Kernel.State.OPERATING);
    stateMap.put(Kernel.State.OPERATING,
                 new KernelStateProvider(kernelStateOperating));

    kernel = new StandardKernel(eventHub, stateMap);
  }

  @After
  public void tearDown() {
    kernel = null;
    kernelStateShutdown = null;
    kernelStateModelling = null;
    kernelStateOperating = null;
  }

  @Test
  public void testStateSwitchToModelling() {
    kernel.setState(Kernel.State.MODELLING);
    assertEquals(Kernel.State.MODELLING, kernel.getState());
    // Verify that the selected state has been initialized.
    verify(kernelStateModelling, times(1)).initialize();
  }

  @Test
  public void testStateSwitchToOperating() {
    kernel.setState(Kernel.State.OPERATING);
    assertEquals(Kernel.State.OPERATING, kernel.getState());
    // Verify that the selected state has been initialized.
    verify(kernelStateOperating, times(1)).initialize();
  }

  private static class KernelStateProvider
      implements Provider<KernelState> {

    private final KernelState state;

    private KernelStateProvider(KernelState providedState) {
      this.state = Objects.requireNonNull(providedState, "providedState is null");
    }

    @Override
    public KernelState get() {
      return state;
    }
  }
}
