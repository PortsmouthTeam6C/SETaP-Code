import { TextEncoder, TextDecoder } from 'util';
;(globalThis as any).TextEncoder = TextEncoder;
;(globalThis as any).TextDecoder = TextDecoder;

const navigateMock = jest.fn();
jest.mock('react-router-dom', () => {
  const actual = jest.requireActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => navigateMock,
    Link: (props: { to: string; children: React.ReactNode }) => (
      <a href={props.to}>{props.children}</a>
    ),
  };
});

import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Signup from '../signup';
import { signup as signupAPI } from '../../api/login';
import { UserContext } from '../../context/UserContext';

jest.mock('../../api/login');
const mockSignup = jest.mocked(signupAPI);

const mockUpdateUser = jest.fn();
const mockUserContext = {
  isLoggedIn: () => false,
  token: undefined,
  expiry: undefined,
  updateUser: mockUpdateUser,
  tryLogin: jest.fn(),
  logOut: jest.fn(),
  username: undefined,
  email: undefined,
  profilePicture: undefined,
  universityId: undefined,
  universityLogo: undefined,
  universityTheming: undefined,
};

describe('Signup Page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUpdateUser.mockResolvedValue(undefined);
  });

  it('renders centered card layout with logo, inputs and link', () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Signup />
      </UserContext.Provider>
    );

    expect(screen.getByRole('img', { name: /logo/i })).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/username/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign up/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /have an account\? log in/i }))
      .toHaveAttribute('href', '/login');
  });

  it('prevents submission when fields are empty', () => {
    render(
      <UserContext.Provider value={mockUserContext}>
        <Signup />
      </UserContext.Provider>
    );

    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));
    expect(mockSignup).not.toHaveBeenCalled();
    expect(mockUpdateUser).not.toHaveBeenCalled();
    expect(navigateMock).not.toHaveBeenCalled();
  });

  it('calls signup API, updateUser and navigates on success', async () => {
    const fakeResponse = {
      token: 'tok',
      expiry: new Date(),
      username: 'you',
      email: 'e@x.com',
      profilePicture: '',
      universityId: 1,
      universityPicture: '',
      theming: '{}',
    };
    mockSignup.mockResolvedValueOnce(fakeResponse);

    render(
      <UserContext.Provider value={mockUserContext}>
        <Signup />
      </UserContext.Provider>
    );

    fireEvent.change(screen.getByPlaceholderText(/email/i), {
      target: { value: 'a@b.com' },
    });
    fireEvent.change(screen.getByPlaceholderText(/username/i), {
      target: { value: 'newuser' },
    });
    fireEvent.change(screen.getByPlaceholderText(/password/i), {
      target: { value: 'hunter2' },
    });
    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(mockSignup).toHaveBeenCalledWith('a@b.com', 'newuser', 'hunter2');
      expect(mockUpdateUser).toHaveBeenCalledWith(fakeResponse);
      expect(navigateMock).toHaveBeenCalledWith('/browse');
    });
  });
});
