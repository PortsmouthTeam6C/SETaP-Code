import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Home from '../home';
import { UserContext } from '../../context/UserContext';

jest.mock('../../api/societies', () => ({
  getJoinedSocieties: jest.fn(() => Promise.resolve([
    {
      societyId: 's1',
      societyName: 'Test Society',
      societyDescription: 'A mock society for testing.',
      societyPicture: '/test.jpg'
    }
  ])),
}));

// mock dependencies
jest.mock('../../components/homepage/societypane', () => ({
  SocietyPane: () => <div data-testid="society-pane">Society Pane</div>,
}));

jest.mock('../../components/homepage/eventpane', () => ({
  EventsPane: () => <div data-testid="events-pane">Events Pane</div>,
}));

jest.mock('../../components/homepage/chatpane', () => ({
  __esModule: true,
  default: () => <div data-testid="chat-pane">Chat Pane</div>,
}));

const mockUserContext = {
  isLoggedIn: () => true,
  token: 'test-token',
  expiry: new Date(),
  universityId: 1,
  updateUser: jest.fn(),
  tryLogin: jest.fn(),
  logOut: jest.fn(),
  username: 'testuser',
  email: 'test@example.com',
  profilePicture: '',
  universityLogo: '',
  universityTheming: { primarycolor: '#0000ff' },
};

describe('Home Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the three-pane layout', async () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Home />
      </UserContext.Provider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('society-pane')).toBeInTheDocument();
      expect(screen.getByTestId('chat-pane')).toBeInTheDocument();
      expect(screen.getByTestId('events-pane')).toBeInTheDocument();
    });
  });

  it('shows modal when "Create an Event" is clicked', async () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Home />
      </UserContext.Provider>
    );

    await waitFor(() => {
      fireEvent.click(screen.getByText(/create an event/i));
    });

    expect(screen.getByText(/create event/i)).toBeInTheDocument();
  });

  it('modal form has all required fields', async () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Home />
      </UserContext.Provider>
    );

    await waitFor(() => {
      fireEvent.click(screen.getByText(/create an event/i));
    });

    expect(screen.getByLabelText(/event name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/location/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/price/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/image/i)).toBeInTheDocument();
  });

  it('simulates submit and reload', async () => {
    const reloadSpy = jest.fn();
    Object.defineProperty(window, 'location', {
      value: { ...window.location, reload: reloadSpy },
      writable: true,
    });

    render(
      <UserContext.Provider value={mockUserContext}>
        <Home />
      </UserContext.Provider>
    );

    await waitFor(() => {
      fireEvent.click(screen.getByText(/create an event/i));
    });
    fireEvent.change(screen.getByLabelText(/event name/i), {
    target: { value: 'Test Event' }
    });
    fireEvent.change(screen.getByLabelText(/description/i), {
      target: { value: 'A great event!' }
    });
    fireEvent.change(screen.getByLabelText(/date/i), {
      target: { value: '2025-12-25' }
    });
    fireEvent.change(screen.getByLabelText(/location/i), {
      target: { value: 'Campus Hall' }
    });
    fireEvent.change(screen.getByLabelText(/price/i), {
      target: { value: '10' }
    });

    const file = new File(['dummy image content'], 'test.png', { type: 'image/png' });
    const input = screen.getByLabelText(/image/i);
    fireEvent.change(input, {
      target: { files: [file] }
    });

    fireEvent.click(screen.getByText(/submit/i));

    // verify
    await waitFor(() => {
    expect(reloadSpy).toHaveBeenCalled();
    });
  });

  it('closes modal when close button is clicked', async () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Home />
      </UserContext.Provider>
    );

    await waitFor(() => {
      fireEvent.click(screen.getByText(/create an event/i));
    });

    fireEvent.click(screen.getByText('X'));

    await waitFor(() => {
      expect(screen.queryByText(/create event/i)).not.toBeInTheDocument();
    });
  });
});
