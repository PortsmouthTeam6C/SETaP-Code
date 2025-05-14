// src/pages/__tests__/browse.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Browse from '../browse';
import { UserContext } from '../../context/UserContext';
import * as SocietiesAPI from '../../api/societies';

jest.mock('../../api/societies');

const mockGetAll = jest.mocked(SocietiesAPI.getAllSocieties);
const mockJoin   = jest.mocked(SocietiesAPI.joinSociety);

jest.mock('../../components/browse/societypane', () => ({
  SocietyPane: () => <div data-testid="society-pane">Society Pane</div>,
}));

const mockUserContext = {
  isLoggedIn:       () => true,
  token:            'fake-token',
  expiry:           new Date(),
  universityId:     42,
  updateUser:       jest.fn(),
  tryLogin:         jest.fn(),
  logOut:           jest.fn(),
  username:         'tester',
  email:            'test@example.com',
  profilePicture:   '',
  universityLogo:   '',
  universityTheming:{ primarycolor: '#123456' },
};

describe('Browse Page', () => {
  // Stub window.alert globally
  beforeAll(() => {
    window.alert = jest.fn();
  });

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders two-column layout with society pane and details area', async () => {
    mockGetAll.mockResolvedValueOnce([
      {
        societyId:          1,
        universityId:       42,
        societyName:        'Test Society',
        societyDescription: 'A mock society for testing browse.',
        societyPicture:     '/test.jpg',
      },
    ]);

    render(
      <UserContext.Provider value={mockUserContext}>
        <Browse />
      </UserContext.Provider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('society-pane')).toBeInTheDocument();
      expect(
        screen.getByRole('heading', { name: /about test society/i })
      ).toBeInTheDocument();
      expect(
        screen.getByText(/a mock society for testing browse\./i)
      ).toBeInTheDocument();
    });

    expect(
      screen.getByRole('button', { name: /join test society/i })
    ).toBeInTheDocument();
  });

  it('invokes joinSociety API when Join button is clicked', async () => {
    mockGetAll.mockResolvedValueOnce([
      {
        societyId:          1,
        universityId:       42,
        societyName:        'Test Society',
        societyDescription: 'A mock society for testing browse.',
        societyPicture:     '/test.jpg',
      },
    ]);
    mockJoin.mockResolvedValueOnce(true);

    render(
      <UserContext.Provider value={mockUserContext}>
        <Browse />
      </UserContext.Provider>
    );

    // click the Join button
    await waitFor(() => {
      fireEvent.click(
        screen.getByRole('button', { name: /join test society/i })
      );
    });

    // now expect joinSociety called with number 1 (not 's1')
    await waitFor(() => {
      expect(mockJoin).toHaveBeenCalledWith(
        mockUserContext.token,
        mockUserContext.expiry,
        1
      );
    });

    // and since we stubbed alert, we can assert it was shown
    expect(window.alert).toHaveBeenCalledWith(
      'Successfully joined society.'
    );
  });

  it('handles empty society list gracefully', async () => {
    mockGetAll.mockResolvedValueOnce(undefined);

    render(
      <UserContext.Provider value={mockUserContext}>
        <Browse />
      </UserContext.Provider>
    );

    await waitFor(() => {
      expect(screen.queryByTestId('society-pane')).not.toBeInTheDocument();
      expect(screen.queryByRole('heading')).not.toBeInTheDocument();
    });
  });

  it('accessibility: Join button is focusable and has accessible name', async () => {
    mockGetAll.mockResolvedValueOnce([
      {
        societyId:          1,
        universityId:       42,
        societyName:        'Test Society',
        societyDescription: 'A mock society for testing browse.',
        societyPicture:     '/test.jpg',
      },
    ]);

    render(
      <UserContext.Provider value={mockUserContext}>
        <Browse />
      </UserContext.Provider>
    );

    const btn = await screen.findByRole('button', { name: /join test society/i });
    btn.focus();
    expect(btn).toHaveFocus();
    expect(btn).toHaveAccessibleName('Join Test Society');
  });
});
